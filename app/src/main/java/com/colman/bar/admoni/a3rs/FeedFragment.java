package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

    public FeedFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        Button button = v.findViewById(R.id.goToPostButton);
        button.setOnClickListener(bV -> {
            Log.d(Consts.TAG, "CLICK!");
            Navigation.findNavController(bV).navigate(R.id.action_feedFragment_to_postViewFragment);

        });

        Button logoffButton = v.findViewById(R.id.logoffButton);
        logoffButton.setOnClickListener(this::handleLogoff);
        v.findViewById(R.id.loadDataButton).setOnClickListener(this::handleLoadData);
        v.findViewById(R.id.createDataButton).setOnClickListener(this::handleCreateData);

        return v;
    }

    public void handleLogoff(View v) {
        mAuth.signOut();
        Intent i = new Intent(getActivity(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
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