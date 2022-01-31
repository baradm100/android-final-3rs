package com.colman.bar.admoni.a3rs;

import static com.colman.bar.admoni.a3rs.Consts.TAG;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.colman.bar.admoni.a3rs.models.Post;
import com.colman.bar.admoni.a3rs.models.SerializableLatLng;
import com.colman.bar.admoni.a3rs.providers.PostProvider;
import com.colman.bar.admoni.a3rs.utils.StringsUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NewPostActivity extends AppCompatActivity {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    public final static String ARG_POST = "post";
    public final static String ARG_POST_ID = "postId";
    private final static int SELECT_PICTURE = 200;


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private String postId;
    private Post postToEdit;
    private LatLng latLng;
    private boolean wasImageSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        latLng = null;
        wasImageSelected = false;
        if (getIntent().getSerializableExtra(ARG_POST) == null) {
            return;
        }
        Log.d(TAG, "GOT POST!!");
        postToEdit = (Post) getIntent().getSerializableExtra(ARG_POST);
        postId = getIntent().getStringExtra(ARG_POST_ID);

        if (postToEdit == null || postId == null) {
            return;
        }

        latLng = postToEdit.getGeoPoint().to();
        wasImageSelected = true;


        // Loading post data
        findViewById(R.id.newPostDeleteButton).setVisibility(View.VISIBLE);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        EditText newPostAdressEditText = findViewById(R.id.newPostAdressEditText);
        ImageView newPostImageView = findViewById(R.id.newPostImageView);


        newPostPostTitleEditText.setText(postToEdit.getTitle());
        newPostPostSubTitleEditText.setText(postToEdit.getSubTitle());
        newPostDescriptionEditText.setText(postToEdit.getDescription());
        newPostPhoneEditText.setText(postToEdit.getUserPhone());
        newPostAdressEditText.setText(postToEdit.getAddressName());
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

    public void handleAdressClick(View v) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleClickSave(View v) {
        if (!validateForm()) {
            return;
        }
        showLoading();

        Log.d(TAG, "Post is valid, creating");
        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        EditText newPostAdressEditText = findViewById(R.id.newPostAdressEditText);
        ImageView newPostImageView = findViewById(R.id.newPostImageView);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Post newPost = new Post(newPostPostTitleEditText.getText().toString(),
                newPostPostSubTitleEditText.getText().toString(),
                newPostDescriptionEditText.getText().toString(),
                currentUser.getDisplayName(),
                newPostPhoneEditText.getText().toString(),
                currentUser.getUid(),
                null, // Will be set by the backend
                newPostAdressEditText.getText().toString(),
                new SerializableLatLng(latLng));


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
                Log.w(TAG, "Error save post", err);
                hideLoading();
                Toast.makeText(NewPostActivity.this, "Failed to post product",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Log.d(TAG, "Post was saved: " + postID);

            Bitmap bitmap = ((BitmapDrawable) newPostImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference storageRef = storage.getReference();
            StorageReference productImageRef = storageRef.child("images/" + postID + ".jpg");

            UploadTask uploadTask = productImageRef.putBytes(data);

            uploadTask.addOnFailureListener(e -> {
                Log.w(TAG, "Failed to upload a file", e);
            }).addOnSuccessListener(snap -> {
                Log.w(TAG, "Image uploaded: " + snap.getTotalByteCount());
                Toast.makeText(NewPostActivity.this, "Product was posted!",
                        Toast.LENGTH_SHORT).show();
                Intent resultData = new Intent(this, NewPostActivity.class);
                resultData.putExtra(ARG_POST, newPost);
                setResult(RESULT_OK, resultData);
                finish();
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleDeleteClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(NewPostActivity.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes", (dialog, which) -> {
            Log.d(TAG, "Delete requested: " + postId);

            showLoading();

            dialog.dismiss();

            CompletableFuture<Boolean> future = PostProvider.deletePost(postId);

            future.whenComplete((postID, err) -> {
                if (err != null) {
                    Log.w(TAG, "Error save post", err);
                    hideLoading();
                    Toast.makeText(NewPostActivity.this, "Failed to post product",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                Log.d(TAG, "Post was deleted: " + postID);

                StorageReference storageRef = storage.getReference();
                StorageReference productImageRef = storageRef.child("images/" + postId + ".jpg");
                productImageRef.delete().addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Image was deleted");
                    setResult(RESULT_CANCELED);
                    finish();
                }).addOnFailureListener(error -> {
                    // Silent failure
                    Log.w(TAG, "Image failed", error);
                    setResult(RESULT_CANCELED);
                    finish();
                });


            });
        });
        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alert.show();

    }

    public void handleImageClick(View v) {
        Log.d(TAG, "IMAGE WAS CLICKED!");
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

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            // Get the url of the image from data
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // update the preview image in the layout
                ImageView newPostImageView = findViewById(R.id.newPostImageView);

                newPostImageView.setImageURI(selectedImageUri);
                wasImageSelected = true;

            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                latLng = place.getLatLng();
                EditText newPostAdressEditText = findViewById(R.id.newPostAdressEditText);
                newPostAdressEditText.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
                Toast.makeText(NewPostActivity.this, "Failed to select an address",
                        Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void showLoading() {
        ProgressBar newPostProgressBar = findViewById(R.id.newPostProgressBar);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        EditText newPostAdressEditText = findViewById(R.id.newPostAdressEditText);
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
        newPostAdressEditText.setEnabled(false);

        newPostProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        ProgressBar newPostProgressBar = findViewById(R.id.newPostProgressBar);

        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostAdressEditText = findViewById(R.id.newPostAdressEditText);
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
        newPostAdressEditText.setEnabled(true);

        newPostProgressBar.setVisibility(View.INVISIBLE);
    }

    private boolean validateForm() {
        boolean isValid = true;
        EditText newPostPostTitleEditText = findViewById(R.id.newPostPostTitleEditText);
        EditText newPostPostSubTitleEditText = findViewById(R.id.newPostPostSubTitleEditText);
        EditText newPostDescriptionEditText = findViewById(R.id.newPostDescriptionEditText);
        EditText newPostPhoneEditText = findViewById(R.id.newPostPhoneEditText);
        EditText newPostAdressEditText = findViewById(R.id.newPostAdressEditText);


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

        if (StringsUtil.isEmpty(newPostAdressEditText.getText().toString())) {
            isValid = false;
            newPostAdressEditText.setError("Address is a required field");
            newPostAdressEditText.requestFocus();
        }

        if (latLng == null) {
            isValid = false;
            newPostAdressEditText.setError("Address is a required field");
            newPostAdressEditText.requestFocus();
        }

        if (!wasImageSelected) {
            isValid = false;
            AlertDialog.Builder alert = new AlertDialog.Builder(NewPostActivity.this);
            alert.setTitle("Missing Image");
            alert.setMessage("Image is a required field");
            alert.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
            });
            alert.show();
        }


        return isValid;

    }
}