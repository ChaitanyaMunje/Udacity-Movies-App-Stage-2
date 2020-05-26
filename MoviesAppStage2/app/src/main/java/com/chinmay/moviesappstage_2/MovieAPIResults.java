package com.chinmay.moviesappstage_2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("ALL")
public class MovieAPIResults {

    @SerializedName("results")
    @Expose
    private List<MovieData> mResults;

    public interface DataAcquiredListener {
        void onMovieDataAcquired(List<MovieData> movies);
    }

    public List<MovieData> getMovies() {
        return mResults;
    }
}
