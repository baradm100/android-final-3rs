package com.colman.bar.admoni.a3rs.models;

import android.util.Log;

import com.colman.bar.admoni.a3rs.Consts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ModelFirebase {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();


}
