package com.colman.bar.admoni.a3rs;

import static com.colman.bar.admoni.a3rs.Consts.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FeedActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ActivityResultLauncher<Intent> mStartForResult;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "User is NOT logged in!");
            Toast.makeText(FeedActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        }


        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(TAG, "Post created!");
                        loadNewData();
                    }
                });
    }

    public void handleMyFeedClick(View v) {
        Log.d(TAG, "My feed click!");
        Intent i = new Intent(this, MyFeedActivity.class);
        mStartForResult.launch(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadNewData() {
        FragmentManager fm = getSupportFragmentManager();
        // Is it a hack? Yes, Does it work? Yes!
        FeedFragment feedFragment = (FeedFragment) fm.getFragments().get(0).getChildFragmentManager().getFragments().get(0);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.postsSwipeRefreshLayout);
        refreshLayout.setRefreshing(true);
        feedFragment.loadData();
    }

    public void handleLogoff(View v) {
        mAuth.signOut();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void handleAddPostClick(View v) {
        Intent i = new Intent(this, NewPostActivity.class);
        mStartForResult.launch(i);
    }

}