package com.chinmay.moviesappstage_2.review_Utils;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chinmay.moviesappstage_2.R;

import java.util.List;

@SuppressWarnings("ALL")
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>{
    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviews) {
        reviewList = reviews;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View reviewView = inflater.inflate(R.layout.list_item_review, parent, false);
        return new ReviewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.review_txt_view.setText(review.getContent());
        holder.author_txt_view.setText(review.getAuthor());

    }


    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {

        public TextView review_txt_view;
        public TextView author_txt_view;

        public ReviewHolder(View itemView) {
            super(itemView);
            review_txt_view = itemView.findViewById(R.id.list_item_review_textview);
            author_txt_view=itemView.findViewById(R.id.author_name);
            review_txt_view.setMovementMethod(new ScrollingMovementMethod());
        }
    }
}
