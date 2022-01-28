package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

}