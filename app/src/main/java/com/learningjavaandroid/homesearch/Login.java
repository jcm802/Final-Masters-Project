package com.learningjavaandroid.homesearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.learningjavaandroid.homesearch.util.UserAPI;

public class Login extends AppCompatActivity {

    private Button skipButton;
    private Button createAccountButton;
    private Button loginButton;
    private AutoCompleteTextView emailAddressEditText;
    private EditText passwordEditText;
    private ProgressBar loginProgress;


    // ======== FIREBASE SETUP ======= //
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ====== FIREBASE SETUP ====== //
        firebaseAuth = FirebaseAuth.getInstance();


        // ====== LINK WIDGETS ======= //
        skipButton = findViewById(R.id.skip_login_button);
        createAccountButton = findViewById(R.id.create_account_button);
        loginButton = findViewById(R.id.login_button);
        emailAddressEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginProgress = findViewById(R.id.login_progress);

        // ========= CLICK LISTENERS ======== //
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MapsActivity.class));
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CreateAccount.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPasswordUser(emailAddressEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
            }

            // ======= HANDLES FIREBASE LOGIN ===== //
            private void loginEmailPasswordUser(String email, String pwd) {
                loginProgress.setVisibility(View.VISIBLE);
                // check fields are not empty
                if(!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(pwd)){
                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    // get the current user
                                    assert user != null;
                                    String currentUserId = user.getUid();
                                    // loop to find the correct user by id
                                    collectionReference
                                            .whereEqualTo("userId", currentUserId)
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    // if there isn't an error
                                                    if(error != null){
                                                    }
                                                    assert value != null;
                                                    if(!value.isEmpty()){ // and if the snapshot isn't empty
                                                        loginProgress.setVisibility(View.INVISIBLE);
                                                        for(QueryDocumentSnapshot snapshot : value){
                                                            // set the current user
                                                            UserAPI userAPI = UserAPI.getInstance();
                                                            userAPI.setUsername(snapshot.getString("username"));
                                                            userAPI.setUserId(snapshot.getString("userId"));
                                                            Toast.makeText(Login.this, "Logged In", Toast.LENGTH_SHORT).show();
                                                            // go to maps activity
                                                            startActivity(new Intent(Login.this,
                                                                    MapsActivity.class));
                                                            finish();
                                                        }

                                                    }
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    });
                } else {
                    loginProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(Login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}