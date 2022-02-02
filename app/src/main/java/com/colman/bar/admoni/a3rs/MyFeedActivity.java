package com.colman.bar.admoni.a3rs;

import static com.colman.bar.admoni.a3rs.Consts.TAG;
import static com.colman.bar.admoni.a3rs.FeedFragment.ARG_IS_MY_FEED;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MyFeedActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> mStartForResult;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feed);

        Bundle myFeedBundle = new Bundle();
        myFeedBundle.putBoolean(ARG_IS_MY_FEED, true);


        // Set the normal navigation flow - but set it as my feed
        NavHostFragment finalHost = NavHostFragment.create(R.navigation.my_nav, myFeedBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.myFeedFragmentContainerView, finalHost)
                .setPrimaryNavigationFragment(finalHost) // equivalent to app:defaultNavHost="true"
                .commit();

        mStartForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(TAG, "Post created!");
                        loadNewData();
                        setResult(RESULT_OK);
                    }
                });

    }

    public void handleAddClick(View v) {
        Intent i = new Intent(this, NewPostActivity.class);
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

}