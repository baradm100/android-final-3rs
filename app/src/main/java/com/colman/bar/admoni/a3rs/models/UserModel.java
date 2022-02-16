package com.colman.bar.admoni.a3rs.models;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserModel {
    public static final UserModel instance = new UserModel();
    Executor executor = Executors.newFixedThreadPool(1);
    Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    ModelFirebase modelFirebase = new ModelFirebase();

    public Boolean isLoggedIn() {

        return modelFirebase.currentUser != null;
    }

    public String getDisplayName(){
        return modelFirebase.currentUser.getDisplayName();
    }
}
