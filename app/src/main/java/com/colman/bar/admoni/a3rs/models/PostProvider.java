package com.colman.bar.admoni.a3rs.models;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.colman.bar.admoni.a3rs.Consts;
import com.colman.bar.admoni.a3rs.MyApplication;
import com.colman.bar.admoni.a3rs.providers.PostIdPair;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PostProvider {
    private static final Executor executor = Executors.newFixedThreadPool(1);

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
    public static CompletableFuture<String> updatePost(String postId, Post updatedPost) {
        CompletableFuture<String> future = new CompletableFuture<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Consts.POSTS_COLLECTION)
                .document(postId)
                .update(updatedPost.to())
                .addOnSuccessListener(documentReference -> {
                    Log.d(Consts.TAG, "DocumentSnapshot updated with ID: " + postId);
                    future.complete(postId);
                })
                .addOnFailureListener(e -> {
                    Log.w(Consts.TAG, "Error updating document", e);
                    future.completeExceptionally(e);
                });

        return future;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<Boolean> deletePost(String postId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Consts.POSTS_COLLECTION)
                .document(postId)
                .delete()
                .addOnSuccessListener(documentReference -> {
                    Log.d(Consts.TAG, "DocumentSnapshot deleted with ID: " + postId);
                    future.complete(true);
                })
                .addOnFailureListener(e -> {
                    Log.w(Consts.TAG, "Error updating document", e);
                    future.completeExceptionally(e);
                });

        return future;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<List<PostIdPair>> getPosts() {
        CompletableFuture<List<PostIdPair>> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Long lastUpdateDate = MyApplication
                .getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("StudentsLastUpdateDate", 0L);

        db.collection(Consts.POSTS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("createdAt", new Date(lastUpdateDate))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        executor.execute(() -> {
                            long lud = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = Post.from(document.getData(), document.getId());
                                AppLocalDb.db.postDao().insertAll(post);

                                if (post.getCreatedAt().getTime() > lud) {
                                    lud = post.getCreatedAt().getTime();
                                }
                            }

                            MyApplication.getContext()
                                    .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                    .edit()
                                    .putLong("StudentsLastUpdateDate", lud)
                                    .commit();

                            List<Post> postsFromCache = AppLocalDb.db.postDao().getAll();
                            future.complete(postsFromCache.stream()
                                    .map(p -> new PostIdPair(p.getId(), p))
                                    .sorted(Comparator.comparing(PostIdPair::getPostCreatedAt).reversed())
                                    .collect(Collectors.toList()));
                        });
                    } else {
                        Log.w(Consts.TAG, "Error getting documents.", task.getException());
                        future.completeExceptionally(task.getException());
                    }
                });


        return future;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<List<PostIdPair>> getPostsForUser(String userUid) {
        CompletableFuture<List<PostIdPair>> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Consts.POSTS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .whereEqualTo("userUid", userUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<PostIdPair> postsPairs = new LinkedList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            postsPairs.add(new PostIdPair(document.getId(), Post.from(document.getData(), document.getId())));
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
