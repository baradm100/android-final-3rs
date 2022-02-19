package com.colman.bar.admoni.a3rs;

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
import com.colman.bar.admoni.a3rs.models.PostProvider;
import com.colman.bar.admoni.a3rs.models.UserModel;
import com.colman.bar.admoni.a3rs.providers.PostIdPair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    public static final String ARG_IS_MY_FEED = "isMyFeed";

    private RecyclerView postsList;
    private PostAdapter postAdapter;
    private boolean isMyFeed = false;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMyFeed = false;

        if (getArguments() != null) {
            Log.d(Consts.TAG, "FeedFragment got args!");
            isMyFeed = getArguments().getBoolean(ARG_IS_MY_FEED);
            Log.d(Consts.TAG, "isMyFeed=" + isMyFeed);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        postsList = v.findViewById(R.id.postsRecyclerView);

        postAdapter = new PostAdapter();
        postsList.setAdapter(postAdapter);
        postsList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.postsSwipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        loadData(v);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData(v);
        });

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadData(View v) {
        SwipeRefreshLayout swipeRefreshLayout = v.findViewById(R.id.postsSwipeRefreshLayout);
        SwipeRefreshLayout postsEmptySwipeRefreshLayout = v.findViewById(R.id.postsEmptySwipeRefreshLayout);
        String userUid = UserModel.instance.getUid();

        CompletableFuture<List<PostIdPair>> future = isMyFeed ? PostProvider.getPostsForUser(userUid) : PostProvider.getPosts();
        future.whenComplete((postPairs, err) -> {
            swipeRefreshLayout.setRefreshing(false);
            postsEmptySwipeRefreshLayout.setRefreshing(false);

            if (err != null) {
                Log.w(Consts.TAG, "Error getting documents.", err);

                Toast.makeText(this.getContext(), "Failed to get posts.",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            // Run on the UI thread to update the view
            getActivity().runOnUiThread(() -> postAdapter.setPosts(postPairs));

            if (postPairs.isEmpty()) {
                swipeRefreshLayout.setVisibility(View.GONE);
                postsEmptySwipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                postsEmptySwipeRefreshLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadData() {
        loadData(getView());
    }

}