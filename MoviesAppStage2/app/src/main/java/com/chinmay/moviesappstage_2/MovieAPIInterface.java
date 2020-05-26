package com.chinmay.moviesappstage_2;

import com.chinmay.moviesappstage_2.review_Utils.ReviewResults;
import com.chinmay.moviesappstage_2.video_Utils.VideoResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@SuppressWarnings("ALL")
public interface MovieAPIInterface {
    @GET("{sortOrder}")
    Call<MovieAPIResults> getMovieData(
            @Path("sortOrder") String sortOrder,
            @Query("api_key") String apiKey
    );


    @GET("{id}/videos")
    Call<VideoResults> getVideoData(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );

    @GET("{id}/reviews")
    Call<ReviewResults> getReviewData(
            @Path("id") int id,
            @Query("api_key") String apiKey
    );
}
