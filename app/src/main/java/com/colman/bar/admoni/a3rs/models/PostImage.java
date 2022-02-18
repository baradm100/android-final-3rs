package com.colman.bar.admoni.a3rs.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.function.Consumer;

public class PostImage implements Serializable {
    private static final ModelFirebase modelFirebase = new ModelFirebase();

    public static class PostImageSuccessResponse {
        private final long totalByteCount;

        public PostImageSuccessResponse(long totalByteCount) {
            this.totalByteCount = totalByteCount;
        }

        public long getTotalByteCount() {
            return totalByteCount;
        }
    }

    private final String postId;

    public PostImage(String postId) {
        this.postId = postId;
    }

    public Object getDrawable() {
        StorageReference storageRef = modelFirebase.storage.getReference();
        return storageRef.child("images/" + postId + ".jpg");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void uploadImage(byte[] bytes, Consumer<Exception> onFailureListener, Consumer<PostImageSuccessResponse> onSuccessListener) {
        StorageReference storageRef = modelFirebase.storage.getReference();
        StorageReference productImageRef = storageRef.child("images/" + postId + ".jpg");

        UploadTask uploadTask = productImageRef.putBytes(bytes);

        uploadTask
                .addOnFailureListener(onFailureListener::accept)
                .addOnSuccessListener(snap -> {
                    PostImageSuccessResponse response = new PostImageSuccessResponse(snap.getTotalByteCount());
                    onSuccessListener.accept(response);
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteImage(Consumer<Exception> onFailureListener, Runnable onSuccessListener) {
        StorageReference storageRef = modelFirebase.storage.getReference();
        StorageReference productImageRef = storageRef.child("images/" + postId + ".jpg");
        productImageRef.delete()
                .addOnSuccessListener(aVoid -> onSuccessListener.run())
                .addOnFailureListener(onFailureListener::accept);

    }

}
