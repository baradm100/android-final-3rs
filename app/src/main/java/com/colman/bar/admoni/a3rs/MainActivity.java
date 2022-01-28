package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(Consts.TAG, "User is logged in!");
            moveToFeed();
            return;
        } else {
            Log.d(Consts.TAG, "User is NOT logged in!");
        }

        EditText emailTextEdit = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        ProgressBar loginProgressBar = findViewById(R.id.loginProgressBar);

        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            if (!validateForm(emailTextEdit, passwordEditText)) {
                return;
            }

            String email = emailTextEdit.getText().toString();
            String password = passwordEditText.getText().toString();

            showLoading(emailTextEdit, passwordEditText, loginProgressBar, loginButton, registerButton);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Consts.TAG, "signInWithEmail:success");
                            moveToFeed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Consts.TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideLoading(emailTextEdit, passwordEditText, loginProgressBar, loginButton, registerButton);
                        }
                    });
        });

        registerButton.setOnClickListener(v -> {
            if (!validateForm(emailTextEdit, passwordEditText)) {
                return;
            }

            String email = emailTextEdit.getText().toString();
            String password = passwordEditText.getText().toString();

            showLoading(emailTextEdit, passwordEditText, loginProgressBar, loginButton, registerButton);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Consts.TAG, "createUserWithEmail:success");
                            moveToFeed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Consts.TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideLoading(emailTextEdit, passwordEditText, loginProgressBar, loginButton, registerButton);
                        }
                    });
        });
    }

    private boolean validateForm(EditText emailTextEdit, EditText passwordEditText) {
        boolean isValid = true;

        if (emailTextEdit.getText().toString().trim().length() == 0) {
            emailTextEdit.setError("Email is a required field");
            isValid = false;
            emailTextEdit.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailTextEdit.getText().toString()).matches()) {
            emailTextEdit.setError("Email must be in a valid format");
            isValid = false;
            emailTextEdit.requestFocus();
        }

        if (passwordEditText.getText().toString().trim().length() == 0) {
            passwordEditText.setError("Password is a required field");
            if (isValid) passwordEditText.requestFocus();
            isValid = false;
        }


        return isValid;
    }

    private void showLoading(EditText emailTextEdit, EditText passwordEditText, ProgressBar loginProgressBar, Button loginButton, Button registerButton) {
        emailTextEdit.setEnabled(false);
        passwordEditText.setEnabled(false);
        loginProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        loginButton.setVisibility(View.INVISIBLE);
        registerButton.setEnabled(false);
        registerButton.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(EditText emailTextEdit, EditText passwordEditText, ProgressBar loginProgressBar, Button loginButton, Button registerButton) {
        emailTextEdit.setEnabled(true);
        passwordEditText.setEnabled(true);
        loginProgressBar.setVisibility(View.INVISIBLE);
        loginButton.setEnabled(true);
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setEnabled(true);
        registerButton.setVisibility(View.VISIBLE);
    }

    private void moveToFeed() {
        Intent i = new Intent(this, FeedActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}