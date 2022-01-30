package com.colman.bar.admoni.a3rs;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.colman.bar.admoni.a3rs.models.Post;
import com.colman.bar.admoni.a3rs.providers.PostProvider;
import com.colman.bar.admoni.a3rs.utils.StringsUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

public class NewPostActivity extends AppCompatActivity {
    public final static String ARG_POST = "post";

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        if (savedInstanceState == null) {
            return;
        }
        Post postToEdit = (Post) savedInstanceState.getSerializable(ARG_POST);

        if (postToEdit != null) {
            // Showing the delete
            findViewById(R.id.newPostDeleteButton).setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleClickSave(View v) {
        if (!validateForm()) {
            return;
        }
        showLoading();

        Log.d(Consts.TAG, "Post is valid, creating");
        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Post newPost = new Post(newPostPostTitleEditText.getText().toString(),
                newPostPostSubTitleEditText.getText().toString(),
                newPostDescriptionEditText.getText().toString(),
                currentUser.getDisplayName(),
                newPostPhoneEditText.getText().toString(),
                currentUser.getUid(),
                null);


        CompletableFuture<String> future = PostProvider.savePost(newPost);
        future.whenComplete((postID, err) -> {
            if (err != null) {
                Log.w(Consts.TAG, "Error save post", err);
                hideLoading();
                Toast.makeText(NewPostActivity.this, "Failed to post product",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Log.d(Consts.TAG, "Post was saved: " + postID);

            Toast.makeText(NewPostActivity.this, "Product was posted!",
                    Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });

    }

    private void showLoading() {
        ProgressBar newPostProgressBar = findViewById(R.id.newPostProgressBar);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        Button newPostSaveButton = findViewById(R.id.newPostSaveButton);
        Button newPostDeleteButton = findViewById(R.id.newPostDeleteButton);

        newPostPostTitleEditText.setEnabled(false);
        newPostPostSubTitleEditText.setEnabled(false);
        newPostDescriptionEditText.setEnabled(false);
        newPostPhoneEditText.setEnabled(false);
        newPostSaveButton.setEnabled(false);
        newPostDeleteButton.setEnabled(false);


        newPostProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        ProgressBar newPostProgressBar = findViewById(R.id.newPostProgressBar);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        Button newPostSaveButton = findViewById(R.id.newPostSaveButton);
        Button newPostDeleteButton = findViewById(R.id.newPostDeleteButton);

        newPostPostTitleEditText.setEnabled(true);
        newPostPostSubTitleEditText.setEnabled(true);
        newPostDescriptionEditText.setEnabled(true);
        newPostPhoneEditText.setEnabled(true);
        newPostSaveButton.setEnabled(true);
        newPostDeleteButton.setEnabled(true);


        newPostProgressBar.setVisibility(View.INVISIBLE);
    }

    private boolean validateForm() {
        boolean isValid = true;
        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);

        if (StringsUtil.isEmpty(newPostPostTitleEditText.getText().toString())) {
            isValid = false;
            newPostPostTitleEditText.setError("Post title is a required field");
            newPostPostTitleEditText.requestFocus();
        }

        if (StringsUtil.isEmpty(newPostPostSubTitleEditText.getText().toString())) {
            isValid = false;
            newPostPostSubTitleEditText.setError("Post sub-title is a required field");
            newPostPostSubTitleEditText.requestFocus();
        }

        if (StringsUtil.isEmpty(newPostDescriptionEditText.getText().toString())) {
            isValid = false;
            newPostDescriptionEditText.setError("Post description is a required field");
            newPostDescriptionEditText.requestFocus();
        }

        if (StringsUtil.isEmpty(newPostPhoneEditText.getText().toString())) {
            isValid = false;
            newPostPhoneEditText.setError("Phone is a required field");
            newPostPhoneEditText.requestFocus();
        } else if (!Patterns.PHONE.matcher(newPostPhoneEditText.getText().toString()).matches()) {
            isValid = false;
            newPostPhoneEditText.setError("Phone must be in a valid format");
            newPostPhoneEditText.requestFocus();
        }


        return isValid;

    }
}