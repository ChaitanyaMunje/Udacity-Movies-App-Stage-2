package com.chinmay.moviesappstage_2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.chinmay.moviesappstage_2.database.AppDatabase;

@SuppressWarnings("ALL")
public class DetailViewModel extends ViewModel {

    private LiveData<MovieData> favorite;

    public DetailViewModel(AppDatabase db, int id) {
        favorite = db.favoriteDao().loadMovieEntry(id);
    }

    public LiveData<MovieData> getFavorite() {
        return favorite;
    }


}
