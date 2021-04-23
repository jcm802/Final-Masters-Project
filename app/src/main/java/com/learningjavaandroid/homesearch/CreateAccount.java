package com.learningjavaandroid.homesearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.learningjavaandroid.homesearch.util.UserAPI;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    Button skipButton;

    // ==== FIREBASE SETUP ==== //
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // ===== FireStore CONNECTION ===== //
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ===== CONNECT COLLECTION REFERENCE ==== //
    private CollectionReference collectionReference = db.collection("users");

    // ===== CONNECT CREATE ACCOUNT WIDGETS ==== //
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button createAccountButton;
    private EditText userNameEditText;
    private EditText locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // ===== FIREBASE AUTHORISATION ===== //
        firebaseAuth = FirebaseAuth.getInstance();

        // ===== SKIP BUTTON ==== //
        skipButton = findViewById(R.id.skip_account_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this, MapsActivity.class));
            }
        });

        createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.create_account_progress);
        userNameEditText = findViewById(R.id.username_account_edit_text);
        emailEditText = findViewById(R.id.email_account_edit_text);
        passwordEditText = findViewById(R.id.password_account_edit_text);
        locationEditText = findViewById(R.id.location_account_edit_text);

        // ===== AUTHENTICATION LISTENER ===== //
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get the current user
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    // User is already logged in
                } else {
                    // No user yet
                }
            }
        };

        // ======= CREATE ACCOUNT BUTTON SET UP ====== //
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty((emailEditText.getText().toString()))
                && !TextUtils.isEmpty(passwordEditText.getText().toString())
                && !TextUtils.isEmpty(userNameEditText.getText().toString())){
                    // if all fields have been entered
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String username = userNameEditText.getText().toString().trim();

                    // ======== CREATE USER ACCOUNT METHOD CALL  ======== //
                    createUserEmailAccount(email, password, username);

                } else {
                    Toast.makeText(CreateAccount.this, "Empty fields not allowed",
                            Toast.LENGTH_SHORT).show();
                }



    }

    // ==== CREATE USER ACCOUNT METHOD ==== //
    private void createUserEmailAccount(String email, String password, String username){
            // Validation
            if(!TextUtils.isEmpty(email)
                    && !TextUtils.isEmpty(password)
                    && !TextUtils.isEmpty(username)){
                // if all required fields have entered continue with create account

                // show progress bar
                progressBar.setVisibility(View.VISIBLE);

                // Authentication handling start
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    // add user to user collections
                                    currentUser = firebaseAuth.getCurrentUser();
                                    assert currentUser != null;
                                    String currentUserId = currentUser.getUid();

                                    // HashMap for retaining collections in db and creating documents
                                    Map<String, String> userObj = new HashMap<>();
                                    userObj.put("userId", currentUserId);
                                    userObj.put("username", username);

                                    // Save to the fireStore collection
                                    collectionReference.add(userObj)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    documentReference.get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if(task.getResult().exists()){
                                                                        // User account has been added successfully, remove progress bar
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        // Retrieve the current users name for display on the next screen
                                                                        String name = task.getResult()
                                                                                .getString("username");

                                                                        UserAPI userAPI = UserAPI.getInstance();
                                                                        userAPI.setUserId(currentUserId);
                                                                        userAPI.setUsername(name);

                                                                        Intent intent = new Intent(CreateAccount.this,
                                                                                MapsActivity.class);
                                                                        // Information to take to maps activity
                                                                        intent.putExtra("username", name);
                                                                        intent.putExtra("userId", currentUserId);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CreateAccount.this, "Failed to add to DB", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateAccount.this,
                                        "Failed to create new user",
                                        Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });
            } else {

            }

    }
        });
    }

    // ===== GET THE CURRENT USER AT THE START ==== //
    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}