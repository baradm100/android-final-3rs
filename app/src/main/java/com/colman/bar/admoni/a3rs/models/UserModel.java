package com.colman.bar.admoni.a3rs.models;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;

import com.colman.bar.admoni.a3rs.Consts;
import com.colman.bar.admoni.a3rs.MainActivity;
import com.colman.bar.admoni.a3rs.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserModel {
    public static final UserModel instance = new UserModel();
    Executor executor = Executors.newFixedThreadPool(1);
    Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    ModelFirebase modelFirebase = new ModelFirebase();

    public Boolean isLoggedIn() {
        return modelFirebase.mAuth.getCurrentUser() != null;
    }

    public String getDisplayName() {
        return modelFirebase.mAuth.getCurrentUser().getDisplayName();
    }

    public void signIn(String email, String password, SignInListener listener) {
        modelFirebase.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Consts.TAG, "signInWithEmail:success");
                            listener.onComplete();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Consts.TAG, "signInWithEmail:failure", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    public void signUp(String email, String password, SignUpListener listener) {
        modelFirebase.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Consts.TAG, "createUserWithEmail:success");
                            listener.onComplete();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Consts.TAG, "createUserWithEmail:failure", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    public void signOut(){
        modelFirebase.mAuth.signOut();
    }

    public void updateDisplayName(String displayName, UpdateDisplayNameListener listener) {
        modelFirebase.mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(displayName).build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(Consts.TAG, "updateDisplayName:success");
                            listener.onComplete();
                        } else {

                            Log.w(Consts.TAG, "updateUser:failure", task.getException());
                            listener.onError();
                        }
                    }
                });
    }

    public interface SignInListener {
        void onComplete();

        void onError();
    }

    public interface SignUpListener {
        void onComplete();

        void onError();
    }

    public interface UpdateDisplayNameListener {
        void onComplete();

        void onError();
    }
}
