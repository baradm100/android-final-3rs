package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.colman.bar.admoni.a3rs.models.Post;
import com.colman.bar.admoni.a3rs.providers.PostIdPair;
import com.colman.bar.admoni.a3rs.providers.PostProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FeedActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d(Consts.TAG, "User is NOT logged in!");
            return;
        }

        TextView welcomeTextView = findViewById(R.id.wellcomeMessage);
        Button logoffButton = findViewById(R.id.logoffButton);

        welcomeTextView.setText(currentUser.getEmail());
        logoffButton.setOnClickListener(this::handleLogoff);
    }

    private void handleLogoff(View v) {
        mAuth.signOut();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleCreateData(View v) {
        Log.d(Consts.TAG, "Create data!!");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Post newPost = new Post("test title", "from code", "desc", currentUser.getEmail(), currentUser.getPhoneNumber(), currentUser.getUid());

        CompletableFuture<String> future = PostProvider.savePost(newPost);
        future.whenComplete((postID, err) -> {
            if (err != null) {
                Log.w(Consts.TAG, "Error save post", err);
                return;
            }

            Log.d(Consts.TAG, "Post was saved: " + postID);
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

}