package com.colman.bar.admoni.a3rs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.colman.bar.admoni.a3rs.models.Post;
import com.colman.bar.admoni.a3rs.providers.PostProvider;
import com.colman.bar.admoni.a3rs.utils.StringsUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

public class NewPostActivity extends AppCompatActivity {
    public final static String ARG_POST = "post";
    public final static String ARG_POST_ID = "postId";
    private final static int SELECT_PICTURE = 200;


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private String postId;
    private Post postToEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        if (getIntent().getSerializableExtra(ARG_POST) == null) {
            return;
        }
        Log.d(Consts.TAG, "GOT POST!!");
        postToEdit = (Post) getIntent().getSerializableExtra(ARG_POST);
        postId = getIntent().getStringExtra(ARG_POST_ID);

        if (postToEdit == null || postId == null) {
            return;
        }

        // Loading post data
        findViewById(R.id.newPostDeleteButton).setVisibility(View.VISIBLE);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        ImageView newPostImageView = findViewById(R.id.newPostImageView);


        newPostPostTitleEditText.setText(postToEdit.getTitle());
        newPostPostSubTitleEditText.setText(postToEdit.getSubTitle());
        newPostDescriptionEditText.setText(postToEdit.getDescription());
        newPostPhoneEditText.setText(postToEdit.getUserPhone());

        StorageReference storageRef = storage.getReference();
        StorageReference productImageRef = storageRef.child("images/" + postId + ".jpg");

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start();

        Glide.with(this)
                .load(productImageRef)
                .placeholder(circularProgressDrawable)
                .into(newPostImageView);

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
        ImageView newPostImageView = findViewById(R.id.newPostImageView);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Post newPost = new Post(newPostPostTitleEditText.getText().toString(),
                newPostPostSubTitleEditText.getText().toString(),
                newPostDescriptionEditText.getText().toString(),
                currentUser.getDisplayName(),
                newPostPhoneEditText.getText().toString(),
                currentUser.getUid(),
                null);


        CompletableFuture<String> future;
        if (postToEdit == null || postId == null) {
            // No post, creating a new one
            future = PostProvider.savePost(newPost);
        } else {
            // Post edit, updating the post
            future = PostProvider.updatePost(postId, newPost);
        }
        future.whenComplete((postID, err) -> {
            if (err != null) {
                Log.w(Consts.TAG, "Error save post", err);
                hideLoading();
                Toast.makeText(NewPostActivity.this, "Failed to post product",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Log.d(Consts.TAG, "Post was saved: " + postID);

            Bitmap bitmap = ((BitmapDrawable) newPostImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference storageRef = storage.getReference();
            StorageReference productImageRef = storageRef.child("images/" + postID + ".jpg");

            UploadTask uploadTask = productImageRef.putBytes(data);

            uploadTask.addOnFailureListener(e -> {
                Log.w(Consts.TAG, "Failed to upload a file", e);
            }).addOnSuccessListener(snap -> {
                Log.w(Consts.TAG, "Image uploaded: " + snap.getTotalByteCount());
                Toast.makeText(NewPostActivity.this, "Product was posted!",
                        Toast.LENGTH_SHORT).show();
                Intent resultData = new Intent(this, NewPostActivity.class);
                resultData.putExtra(ARG_POST, newPost);
                setResult(RESULT_OK, resultData);
                finish();
            });
        });
    }

    public void handleImageClick(View v) {
        Log.d(Consts.TAG, "IMAGE WAS CLICKED!");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                SELECT_PICTURE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    ImageView newPostImageView = findViewById(R.id.newPostImageView);

                    newPostImageView.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private void showLoading() {
        ProgressBar newPostProgressBar = findViewById(R.id.newPostProgressBar);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        ImageView newPostImageView = findViewById(R.id.newPostImageView);
        Button newPostSaveButton = findViewById(R.id.newPostSaveButton);
        Button newPostDeleteButton = findViewById(R.id.newPostDeleteButton);

        newPostPostTitleEditText.setEnabled(false);
        newPostPostSubTitleEditText.setEnabled(false);
        newPostDescriptionEditText.setEnabled(false);
        newPostPhoneEditText.setEnabled(false);
        newPostSaveButton.setEnabled(false);
        newPostDeleteButton.setEnabled(false);
        newPostImageView.setEnabled(false);

        newPostProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        ProgressBar newPostProgressBar = findViewById(R.id.newPostProgressBar);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        ImageView newPostImageView = findViewById(R.id.newPostImageView);
        Button newPostSaveButton = findViewById(R.id.newPostSaveButton);
        Button newPostDeleteButton = findViewById(R.id.newPostDeleteButton);

        newPostPostTitleEditText.setEnabled(true);
        newPostPostSubTitleEditText.setEnabled(true);
        newPostDescriptionEditText.setEnabled(true);
        newPostPhoneEditText.setEnabled(true);
        newPostSaveButton.setEnabled(true);
        newPostDeleteButton.setEnabled(true);
        newPostImageView.setEnabled(true);

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