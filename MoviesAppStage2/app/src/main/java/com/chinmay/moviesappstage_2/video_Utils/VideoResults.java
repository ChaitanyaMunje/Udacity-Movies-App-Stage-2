package com.chinmay.moviesappstage_2.video_Utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("ALL")
public class VideoResults {

    @SerializedName("results")
    @Expose
    private List<Video> videoresults = null;

    public List<Video> getVideoList() {
        return videoresults;
    }

    public interface VideoAcquiredListener {
        void onVideosAcquired(List<Video> videos);
    }
}
