package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.colman.bar.admoni.a3rs.adapters.PostAdapter;
import com.colman.bar.admoni.a3rs.models.Post;
import com.colman.bar.admoni.a3rs.providers.PostIdPair;
import com.colman.bar.admoni.a3rs.providers.PostProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView postsList;

    public FeedFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        postsList = v.findViewById(R.id.postsRecyclerView);

        PostAdapter postAdapter = new PostAdapter();
        postsList.setAdapter(postAdapter);
        postsList.setLayoutManager(new LinearLayoutManager(this.getContext()));


        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.postsSwipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        loadData(postAdapter, swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData(postAdapter, swipeRefreshLayout);
        });

//        v.findViewById(R.id.goToPostButton).setOnClickListener(bV -> {
//            Log.d(Consts.TAG, "CLICK!");
//            Navigation.findNavController(bV).navigate(R.id.action_feedFragment_to_postViewFragment);
//
//        });
//
//        v.findViewById(R.id.logoffButton).setOnClickListener(this::handleLogoff);
//        v.findViewById(R.id.loadDataButton).setOnClickListener(this::handleLoadData);
//        v.findViewById(R.id.createDataButton).setOnClickListener(this::handleCreateData);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData(PostAdapter postAdapter, SwipeRefreshLayout swipeRefreshLayout) {
        CompletableFuture<List<PostIdPair>> future = PostProvider.getPosts();
        future.whenComplete((postPairs, err) -> {
            swipeRefreshLayout.setRefreshing(false);

            if (err != null) {
                Log.w(Consts.TAG, "Error getting documents.", err);

                Toast.makeText(this.getContext(), "Failed to get posts.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            postAdapter.setPosts(postPairs);

            for (PostIdPair postPair : postPairs) {
                Log.d(Consts.TAG, "ID: " + postPair.getId() + "=" + postPair.getPost().toString());
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleLoadData(View v) {
        Log.d(Consts.TAG, "Load data!!");

        CompletableFuture<List<PostIdPair>> future = PostProvider.getPosts();
        future.whenComplete((postPairs, err) -> {
            if (err != null) {
                Log.w(Consts.TAG, "Error getting documents.", err);
                return;
            }

            for (PostIdPair postPair : postPairs) {
                Log.d(Consts.TAG, "ID: " + postPair.getId() + "=" + postPair.getPost().toString());
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleCreateData(View v) {
        Log.d(Consts.TAG, "Create data!!");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Post newPost = new Post("test title", "from code", "desc",
                currentUser.getEmail(), currentUser.getPhoneNumber(), currentUser.getUid());

        CompletableFuture<String> future = PostProvider.savePost(newPost);
        future.whenComplete((postID, err) -> {
            if (err != null) {
                Log.w(Consts.TAG, "Error save post", err);
                return;
            }

            Log.d(Consts.TAG, "Post was saved: " + postID);
        });
    }

}