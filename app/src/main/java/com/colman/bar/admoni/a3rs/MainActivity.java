package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.colman.bar.admoni.a3rs.models.UserModel;
import com.colman.bar.admoni.a3rs.utils.StringsUtil;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private void initPlaces() {
        Log.d(Consts.TAG, "Was init!");

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            Log.w(Consts.TAG, "applicationInfo failed", e);
        }
        String apiKey = null;
        if (applicationInfo != null) {
            apiKey = applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
        }

        Log.d(Consts.TAG, "apiKey=" + apiKey);
        Places.initialize(getApplicationContext(), apiKey);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPlaces();

        setContentView(R.layout.activity_main);

        if (UserModel.instance.isLoggedIn()) {
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

            UserModel.instance.signIn(email, password, new UserModel.SignInListener() {
                @Override
                public void onComplete() {
                    moveToFeed();
                }

                @Override
                public void onError() {
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

            UserModel.instance.signUp(email, password, new UserModel.SignUpListener() {
                @Override
                public void onComplete() {
                    moveToFeed();
                }

                @Override
                public void onError() {
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    hideLoading(emailTextEdit, passwordEditText, loginProgressBar, loginButton, registerButton);
                }
            });

        });
    }

    private boolean validateForm(EditText emailTextEdit, EditText passwordEditText) {
        boolean isValid = true;

        if (StringsUtil.isEmpty(emailTextEdit.getText().toString())) {
            emailTextEdit.setError("Email is a required field");
            isValid = false;
            emailTextEdit.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailTextEdit.getText().toString()).matches()) {
            emailTextEdit.setError("Email must be in a valid format");
            isValid = false;
            emailTextEdit.requestFocus();
        }

        if (StringsUtil.isEmpty(passwordEditText.getText().toString())) {
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

        if (UserModel.instance.isLoggedIn() == false) {
            return;
        }

        if (UserModel.instance.getDisplayName() == null || StringsUtil.isEmpty(UserModel.instance.getDisplayName())) {
            Intent i = new Intent(this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            Intent i = new Intent(this, FeedActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}