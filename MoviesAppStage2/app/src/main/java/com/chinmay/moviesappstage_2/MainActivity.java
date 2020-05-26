package com.chinmay.moviesappstage_2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.chinmay.moviesappstage_2.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String SORT_ORDER = "SortOrder";
    private static final String FAVORITE = "favorite";
    private static final String RECYCLER_POSITION = "RecyclerViewPosition";
    private SharedPreferences mSharedPreferences;
    private String mSortOrder;
    ProgressBar progressBar;

    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private Parcelable recyclerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.progress);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRecyclerView = findViewById(R.id.recycler_view);


        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {


            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mMovieAdapter = new MovieAdapter(getApplicationContext(), new ArrayList<MovieData>());
        mRecyclerView.setAdapter(mMovieAdapter);

        mSortOrder = mSharedPreferences.getString(SORT_ORDER, NetworkUtils.POPULAR);

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());

        setupViewModel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_POSITION,
                mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(RECYCLER_POSITION)) {
            recyclerPosition = savedInstanceState.getParcelable(RECYCLER_POSITION);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem popular = menu.getItem(0);
        MenuItem top_rated = menu.getItem(1);
        MenuItem favorite = menu.getItem(2);


        // Check the correct radio button in the menu.
        switch (mSortOrder) {
            case FAVORITE:
                favorite.setChecked(true);
                break;
            case NetworkUtils.TOP_RATED:
                top_rated.setChecked(true);
                break;
            case NetworkUtils.POPULAR:
                popular.setChecked(true);
                break;
            default:
                popular.setChecked(true);
                break;
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.menu_item_popular:
                mSortOrder = NetworkUtils.POPULAR;
                break;
            case R.id.menu_item_top_rated:
                mSortOrder = NetworkUtils.TOP_RATED;
                break;
            case R.id.menu_item_favorite:
                mSortOrder = FAVORITE;
                break;
            default:
                return false;
        }

        editor.putString(SORT_ORDER, mSortOrder);
        editor.apply();
        setupViewModel();
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mSortOrder.contentEquals(FAVORITE)) {
            setupViewModel();
        }
    }

    public void setupViewModel() {

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);


        viewModel.getFavoriteMovies().observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(@Nullable List<MovieData> favoriteEntries) {
                Log.d(TAG, "Receiving changes from LiveData");

                if (mSortOrder.contentEquals(FAVORITE)) {
                    List<MovieData> movieList = new ArrayList<MovieData>();

                    if (favoriteEntries != null) {
                        for (MovieData fave : favoriteEntries) {
                            fave.setFavorite(1);
                        }
                        setAdapter(favoriteEntries);
                    }
                }
            }
        });

        viewModel.getTopRatedMovies().observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(@Nullable List<MovieData> movieData) {
                if (movieData != null && mSortOrder.contentEquals(NetworkUtils.TOP_RATED)) {
                    setAdapter(movieData);
                }
            }
        });

        viewModel.getPopularMovies().observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(@Nullable List<MovieData> movieData) {
                if (movieData != null && mSortOrder.contentEquals(NetworkUtils.POPULAR)) {
                    setAdapter(movieData);
                }
            }
        });
    }

    public void setAdapter(List<MovieData> movies) {
        progressBar.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMovies(movies);
        mMovieAdapter.notifyDataSetChanged();
        if (recyclerPosition != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerPosition);
        }
    }
}
