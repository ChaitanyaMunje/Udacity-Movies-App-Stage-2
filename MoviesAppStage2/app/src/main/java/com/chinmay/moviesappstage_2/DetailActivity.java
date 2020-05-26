package com.chinmay.moviesappstage_2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chinmay.moviesappstage_2.review_Utils.Review;
import com.chinmay.moviesappstage_2.review_Utils.ReviewAdapter;
import com.chinmay.moviesappstage_2.review_Utils.ReviewResults;
import com.chinmay.moviesappstage_2.video_Utils.Video;
import com.chinmay.moviesappstage_2.video_Utils.VideoAdapter;
import com.chinmay.moviesappstage_2.video_Utils.VideoResults;
import com.chinmay.moviesappstage_2.database.AppDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class DetailActivity extends AppCompatActivity implements
        VideoResults.VideoAcquiredListener,
        ReviewResults.ReviewsAcquiredListener{

    private static final String TAG = "DetailActivity";
    private static final String MOVIE_DATA = "MovieData";
    private static final String FAVORITE = "favorite";

    private AppDatabase mDb;

    private MovieData mMovie;

    private TextView mVideoTextView;
    private TextView review_txt_view;
    private RecyclerView video_recycler_view;
    private RecyclerView review_recycler_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(getApplicationContext());

        TextView release_date_text_view = findViewById(R.id.releaseDataTextView);

        TextView synopsis_text_view = findViewById(R.id.synopsisTextView);
        synopsis_text_view.setMovementMethod(new ScrollingMovementMethod());

        RatingBar rating_bar = findViewById(R.id.ratingBar);
        rating_bar.setIsIndicator(true);
        rating_bar.setNumStars(5);
        rating_bar.setStepSize((float) 0.1);

        ImageView poster_img_view = findViewById(R.id.imageView);
        mVideoTextView = findViewById(R.id.video_label_textview);
        review_txt_view = findViewById(R.id.review_label_textview);
        video_recycler_view = findViewById(R.id.video_recycler_view);
        review_recycler_view = findViewById(R.id.review_recycler_view);

        Bundle data = getIntent().getExtras();

        if (data != null && data.containsKey(MOVIE_DATA)) {
            mMovie = data.getParcelable(MOVIE_DATA);
            setTitle(mMovie.getTitle());
            release_date_text_view.setText(mMovie.getReleaseDate());
            synopsis_text_view.setText(mMovie.getOverview());
            rating_bar.setRating((float) mMovie.getVoteAverage() / (float) 2.0);

            Picasso.get()
                    .load(mMovie.getSmallPosterUrl())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(poster_img_view);
            Log.i(TAG, String.valueOf((float) mMovie.getVoteAverage()));

            setupVideoRecycler(mMovie.getId());
            setupReviewRecycler(mMovie.getId());

            if (mMovie.isFavorite() == 0) {

                DetailViewModelFactory factory = new DetailViewModelFactory(mDb, mMovie.getId());
                final DetailViewModel viewModel = ViewModelProviders.of(this, factory)
                        .get(DetailViewModel.class);

                viewModel.getFavorite().observe(this, new Observer<MovieData>() {
                    @Override
                    public void onChanged(@Nullable MovieData favoriteEntry) {
                        if (viewModel.getFavorite().getValue() != null) {
                            mMovie.setFavorite(1);
                        }
                    }
                });

            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FAVORITE, mMovie.isFavorite() == 1);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean(FAVORITE)) {
            mMovie.setFavorite(1);
        } else {
            mMovie.setFavorite(0);
        }

    }

    public void setupVideoRecycler(int id) {
        Call<VideoResults> call = NetworkUtils.buildVideoCall(id);

        callVideos(call);
        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);

        video_recycler_view.setLayoutManager(videoLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        video_recycler_view.addItemDecoration(itemDecoration);

    }

    public void setupReviewRecycler(int id) {
        Call<ReviewResults> reviewCall = NetworkUtils.buildReviewCall(mMovie.getId());
        callReviews(reviewCall);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);

        review_recycler_view.setLayoutManager(reviewLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        review_recycler_view.addItemDecoration(itemDecoration);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);

        // If the movie is a favorite, color it appropriately.
        if (mMovie.isFavorite() == 1) {
            colorIconRed(menu.getItem(0));
        } else {
            colorIconWhite(menu.getItem(0));
        }

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_favorite:
                changeIconColor(item);
                addMovieToDb();
                return true;
            default:
                return false;
        }
    }

    public void addMovieToDb() {

        final MovieData favoriteEntry = new MovieData(
                mMovie.getTitle(),
                mMovie.getReleaseDate(),
                mMovie.getVoteAverage(),
                mMovie.getPosterPath(),
                mMovie.getOverview(),
                mMovie.getId());

        if (mMovie.isFavorite() == 1) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteDao().insertFavorite(favoriteEntry);
                }
            });
        } else {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteDao().deleteFavorite(favoriteEntry);
                }
            });
        }
    }

    public void changeIconColor(MenuItem item) {


        if (mMovie.isFavorite() == 0) {
            colorIconRed(item);
            mMovie.setFavorite(1);
        } else {
            colorIconWhite(item);
            mMovie.setFavorite(0);
        }
    }


    //Get videos from the video API asynchronously.

    public void callVideos(Call<VideoResults> call) {
        call.enqueue(new Callback<VideoResults>() {
            @Override
            public void onResponse(Call<VideoResults> call, Response<VideoResults> response) {
                if (response.message().contentEquals("OK")) {
                    Log.i(TAG, response.body().getVideoList().toString());
                    onVideosAcquired(response.body().getVideoList());
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<VideoResults> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public void callReviews(Call<ReviewResults> call) {
        call.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Call<ReviewResults> call, Response<ReviewResults> response) {
                if (response.message().contentEquals("OK")) {
                    Log.i(TAG, response.body().getReviewList().toString());
                    onReviewsAcquired(response.body().getReviewList());
                } else Log.e(TAG, response.message());
            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }




    @Override
    public void onVideosAcquired(List<Video> videos) {
        if (!videos.isEmpty()) {
            mVideoTextView.setVisibility(View.VISIBLE);
            video_recycler_view.setAdapter(new VideoAdapter(videos));
        }
    }

    public void onReviewsAcquired(List<Review> reviews) {
        if (!reviews.isEmpty()) {
            review_txt_view.setVisibility(View.VISIBLE);
            review_recycler_view.setAdapter(new ReviewAdapter(reviews));
        }
    }

    public void colorIconRed(MenuItem item) {
        Drawable icon = item.getIcon();
        Drawable newIcon = icon.mutate();
        DrawableCompat.setTint(newIcon, getResources().getColor(R.color.colorAccent));
        DrawableCompat.setTintMode(newIcon, PorterDuff.Mode.SRC_IN);
        item.setIcon(newIcon);
    }

    public void colorIconWhite(MenuItem item) {
        Drawable icon = item.getIcon();
        Drawable newIcon = icon.mutate();
        DrawableCompat.setTint(newIcon, getResources().getColor(R.color.colorwhite));
        DrawableCompat.setTintMode(newIcon, PorterDuff.Mode.SRC_IN);
        item.setIcon(newIcon);

    }
}
