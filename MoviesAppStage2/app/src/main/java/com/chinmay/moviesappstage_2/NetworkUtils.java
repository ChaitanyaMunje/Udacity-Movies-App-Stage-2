package com.chinmay.moviesappstage_2;

import com.chinmay.moviesappstage_2.review_Utils.ReviewResults;
import com.chinmay.moviesappstage_2.video_Utils.VideoResults;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("ALL")
public class NetworkUtils {
    private static final String API_KEY = "6b3748adf9061715d3c0eed091dbf190";
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static MovieAPIInterface sMovieAPIInterface = retrofit.create(MovieAPIInterface.class);



    public static Call<MovieAPIResults> buildAPICall(String sortOrder) {

        Call<MovieAPIResults> call = sMovieAPIInterface.getMovieData(sortOrder, API_KEY);

        return call;
    }

    public static Call<VideoResults> buildVideoCall(int videoId) {

        Call<VideoResults> call = sMovieAPIInterface.getVideoData(videoId, API_KEY);

        return call;
    }

    public static Call<ReviewResults> buildReviewCall(int videoId) {
        return sMovieAPIInterface.getReviewData(videoId, API_KEY);
    }
}
