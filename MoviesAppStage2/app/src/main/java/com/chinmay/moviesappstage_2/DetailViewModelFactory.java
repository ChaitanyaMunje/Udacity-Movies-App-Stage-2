package com.chinmay.moviesappstage_2;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.chinmay.moviesappstage_2.database.AppDatabase;

@SuppressWarnings("ALL")
public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mId;

    public DetailViewModelFactory(AppDatabase db, int id) {
        mDb = db;
        mId = id;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailViewModel(mDb, mId);
    }
}
