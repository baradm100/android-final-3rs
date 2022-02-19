package com.colman.bar.admoni.a3rs.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

public class ModelFirebase {
    public final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public final FirebaseStorage storage = FirebaseStorage.getInstance();
}
