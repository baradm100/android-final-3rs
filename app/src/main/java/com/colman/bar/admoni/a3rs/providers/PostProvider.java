package com.colman.bar.admoni.a3rs.providers;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.colman.bar.admoni.a3rs.Consts;
import com.colman.bar.admoni.a3rs.models.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PostProvider {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<String> savePost(Post post) {
        CompletableFuture<String> future = new CompletableFuture<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Consts.POSTS_COLLECTION)
                .add(post.to())
                .addOnSuccessListener(documentReference -> {
                    Log.d(Consts.TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    future.complete(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w(Consts.TAG, "Error adding document", e);
                    future.completeExceptionally(e);
                });

        return future;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<List<PostIdPair>> getPosts() {
        CompletableFuture<List<PostIdPair>> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Consts.POSTS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<PostIdPair> postsPairs = new LinkedList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            postsPairs.add(new PostIdPair(document.getId(), Post.from(document.getData())));
                        }
                        future.complete(postsPairs);

                    } else {
                        Log.w(Consts.TAG, "Error getting documents.", task.getException());
                        future.completeExceptionally(task.getException());
                    }
                });


        return future;
    }
}
