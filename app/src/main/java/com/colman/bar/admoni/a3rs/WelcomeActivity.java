package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class WelcomeActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void handleSave(View v) {
        EditText welcomeNameTextEdit = findViewById(R.id.welcomeNameTextEdit);

        String name = welcomeNameTextEdit.getText().toString();
        boolean isValid = true;

        if (name.trim().length() == 0) {
            welcomeNameTextEdit.setError("Name is a required field");
            isValid = false;
            welcomeNameTextEdit.requestFocus();
        }


        if (!isValid) {
            return;
        }

        showLoading();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        FirebaseUser user = mAuth.getCurrentUser();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(this, (task) -> {
                    if (task.isSuccessful()) {
                        moveToFeed();
                    } else {
                        hideLoading();
                        Log.w(Consts.TAG, "updateUser:failure", task.getException());
                        Toast.makeText(WelcomeActivity.this, "Update info failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void moveToFeed() {
        Intent i = new Intent(this, FeedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void showLoading() {
        ProgressBar welcomeProgressBar = findViewById(R.id.welcomeProgressBar);
        Button save = findViewById(R.id.saveWelcomeButton);
        EditText name = findViewById(R.id.welcomeNameTextEdit);

        welcomeProgressBar.setVisibility(View.VISIBLE);
        save.setVisibility(View.INVISIBLE);

        name.setEnabled(false);
    }

    private void hideLoading() {
        ProgressBar welcomeProgressBar = findViewById(R.id.welcomeProgressBar);
        Button save = findViewById(R.id.saveWelcomeButton);
        EditText name = findViewById(R.id.welcomeNameTextEdit);

        welcomeProgressBar.setVisibility(View.INVISIBLE);
        save.setVisibility(View.VISIBLE);

        name.setEnabled(true);
    }
}