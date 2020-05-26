package com.chinmay.moviesappstage_2.video_Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinmay.moviesappstage_2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

@SuppressWarnings("ALL")
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder>  {

    private List<Video> videoList;

    public VideoAdapter(List<Video> videos) {
        videoList = videos;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View videoView = inflater.inflate(R.layout.list_item_video, parent, false);


       VideoViewClickListener listener = new VideoViewClickListener() {
            @Override
            public void onClick(View view, int i) {
                String key = videoList.get(i).getKey();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + key));
                context.startActivity(intent);
            }
        };

        VideoHolder videoHolder = new VideoHolder(videoView, listener);
        return videoHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        Video video = videoList.get(position);
        holder.video_txt_view.setText(video.getName());
        String thumbnail_url="http://img.youtube.com/vi/"+video.getKey()+"/hqdefault.jpg";

        Picasso.get().load(thumbnail_url).into(holder.video_img);

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }



    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView video_txt_view;
        private VideoViewClickListener mVideoViewClickListener;
        public ImageView video_img;

        public VideoHolder(View itemView, VideoViewClickListener listener) {
            super(itemView);
            video_txt_view = itemView.findViewById(R.id.list_item_video_textview);
            video_img=itemView.findViewById(R.id.videoImg);

            mVideoViewClickListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mVideoViewClickListener.onClick(v, getAdapterPosition());
        }
    }
}
