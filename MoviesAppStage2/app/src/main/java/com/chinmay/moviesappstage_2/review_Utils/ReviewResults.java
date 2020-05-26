package com.chinmay.moviesappstage_2.review_Utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("ALL")
public class ReviewResults {
    @SerializedName("results")
    @Expose
    private List<Review> reviewresults = null;

    public interface ReviewsAcquiredListener {
        void onReviewsAcquired(List<Review> reviews);
    }

    public List<Review> getReviewList() {
        return reviewresults;
    }


}
