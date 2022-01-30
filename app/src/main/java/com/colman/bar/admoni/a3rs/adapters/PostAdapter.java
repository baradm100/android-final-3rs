package com.colman.bar.admoni.a3rs.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.colman.bar.admoni.a3rs.PostViewFragment;
import com.colman.bar.admoni.a3rs.R;
import com.colman.bar.admoni.a3rs.models.Post;
import com.colman.bar.admoni.a3rs.providers.PostIdPair;

import java.util.Collections;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.CardViewHolder> {
    private List<PostIdPair> posts = Collections.emptyList();

    public void setPosts(List<PostIdPair> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card_view, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.renderPost(posts.get(position));
    }

    @Override
    public int getItemCount() {
        if (posts == null) {
            return 0;
        }

        return posts.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView subTitleTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.postTitleTextView);
            subTitleTextView = itemView.findViewById(R.id.postSubTitleTextView);
        }

        public void renderPost(PostIdPair postIdPair) {
            Post post = postIdPair.getPost();
            this.titleTextView.setText(post.getTitle());
            this.subTitleTextView.setText(post.getSubTitle());

            itemView.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString(PostViewFragment.ARG_POST_ID, postIdPair.getId());
                args.putSerializable(PostViewFragment.ARG_POST, post);
                Navigation.findNavController(v)
                        .navigate(R.id.action_feedFragment_to_postViewFragment, args);
            });
        }
    }
}
