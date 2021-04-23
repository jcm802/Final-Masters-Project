package com.learningjavaandroid.homesearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.learningjavaandroid.homesearch.adapter.CustomInfoWindow;
import com.learningjavaandroid.homesearch.controller.AppController;
import com.learningjavaandroid.homesearch.data.AsyncResponse;
import com.learningjavaandroid.homesearch.data.Repository;
import com.learningjavaandroid.homesearch.model.Properties;
import com.learningjavaandroid.homesearch.model.PropertyViewModel;
import com.learningjavaandroid.homesearch.util.UserAPI;
import com.learningjavaandroid.homesearch.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int CAMERA_REQUEST_CODE = 101;

    RequestQueue queue;

    // Object representation of google map view
    private GoogleMap mMap;

    // Progress bar
    private ProgressBar progressBarMap;

    // ViewModel
    private PropertyViewModel propertyViewModel;
    private List<Properties> propertiesList;

    // Search
    private CardView cardView;
    private EditText postcodeEdit;
    private ImageButton searchButton;
    private ImageButton cameraButton;
    private String code = "sw179qq";

    // Firebase
    private TextView currentUserTextView;
    private String currentUserId;
    private String currentUserName;
    private TextView userLoggedInText;
    private Button signOutTopButton;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        userLoggedInText = findViewById(R.id.logged_in_as_text_view);
        cameraButton = findViewById(R.id.camera_button);

        // ===== RETRIEVES REQUEST QUEUE FROM CONTROLLER ===== //
        queue = AppController.getInstance(this.getApplicationContext())
               .getRequestQueue();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // ========= FIREBASE CONNECTION ======= //
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserTextView = findViewById(R.id.username_text_view);

        // ========= GETTING CURRENT USER INFORMATION FOR DISPLAY ======= //
        // Using userAPI class for use by firebase
        if(UserAPI.getInstance() != null){
            currentUserId = UserAPI.getInstance().getUserId();
            currentUserName = UserAPI.getInstance().getUsername();
            currentUserTextView.setText(currentUserName);
        }

        // ======= LISTENER TO HANDLE ANY AUTH STATE CHANGE ====== //
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){

                } else {
                }
            }
        };

        // ===== SETTING UP PROPERTY VIEW MODEL =====
        propertyViewModel = new ViewModelProvider(this)
                .get(PropertyViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ======= SEARCH SETUP ======= //
        cardView = findViewById(R.id.cardView);
        postcodeEdit = findViewById(R.id.search_value);
        searchButton = findViewById(R.id.search_btn);

        // ======= PROGRESS BAR SETUP ======= //
        progressBarMap = findViewById(R.id.map_progress);

        // ======= BOTTOM NAVIGATION VIEW ====== //
        // retrieve id
        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // create fragment and set to null as default
            Fragment selectedFragment = null;
            // find which icon is clicked
            int id = item.getItemId();
            if(id == R.id.maps_nav_button){
                // clear the propertiesList
                propertiesList.clear();
                // clear map to avoid adding more markers each time
                mMap.clear();
                // show map view
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map, mapFragment)
                .commit();
                // get all markers again (by calling the map fragment again from onCreate())
                mapFragment.getMapAsync(this);
                return true;
            } else if(id == R.id.list_nav_button){
                // select fragment (use newInstance()) instead of creating a new object
                // show list fragment
                selectedFragment = ListFragment.newInstance();
            } else if(id == R.id.sign_out_nav_button){
                if(user != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                    // return to login
                    startActivity(new Intent(MapsActivity.this, Login.class));
                    finish();
                } else {
                    Toast.makeText(MapsActivity.this, "User not signed in", Toast.LENGTH_LONG).show();
                }
            }
            // ensure the selected fragment isn't null, don't do anything if so
            assert selectedFragment != null;
            // For handling switching fragments
            getSupportFragmentManager().beginTransaction()
                    // swap selected fragment with map
            .replace(R.id.map, selectedFragment)
                    // finalise
            .commit();
            // everything was received correctly
            return true;
        });

        // ======== SEARCH SETUP ON CLICK ===== //
        searchButton.setOnClickListener(v -> {
            // hide soft keyboard
            Util.hideSoftKeyboard(v);
            // clear the list
            propertiesList.clear();
            // Get the url and remove spaces
            String postcode = postcodeEdit.getText().toString().trim();
            // check the edit text is not empty
            if(!TextUtils.isEmpty(postcode)){
                // set the postcode in the viewModel
                code = postcode;
                // get the live postcode from the view model
                propertyViewModel.selectPostcode(code);
                // refresh the map and in the process call the getProperties method and retrieve new properties
                onMapReady(mMap);
                // clear the edit text
                postcodeEdit.setText("");
            }
        });

    }

    // ====== STARTS GOOGLE MAPS ===== //
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));

        // ======== SETTING UP VIEW MODEL ======= //
        propertiesList = new ArrayList<>();
        propertiesList.clear();

        // ========== MAIN GET PROPERTIES METHOD REFACTORED (This makes the call to the API) ============ //
        populateMap();

    }

    private void populateMap() {
        // ======== MAIN GET PROPERTIES METHOD ======= //
        mMap.clear();
        Repository.getProperties(new AsyncResponse() {
            @Override
            public void processProperties(List<Properties> properties) {

                // ======== FOR VIEW MODEL =====
                propertiesList = properties;
                // =============================

                for (Properties property : properties) {

                    LatLng location = new LatLng(Double.parseDouble(property.getLat()),
                            Double.parseDouble(property.getLng()));

                    MarkerOptions markerOptions = new MarkerOptions()
                            .title("£" + property.getPrice())
                            .position(location)
                            .icon(BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_CYAN))
                            .snippet(property.getType());

                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

                }

                // ======= FOR VIEW MODEL =======
                propertyViewModel.setSelectedProperties(propertiesList);
                Log.d("SIZE", "populateMap: " + propertiesList.size());
                // ==============================
            }
        }, code);
    }

    // ===== ON START OF APP START THE AUTH LISTENER FOR AUTHENTICATION ===== //
    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // ===== ON APP CLOSURE STOP THE AUTH LISTENER FOR AUTHENTICATION ===== //
    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    // ======== CAMERA ON ACTIVITY FOR RESULT - BASIC FOUNDATION FOR SNAP A SIGN FEATURE ======== //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // process image and extract text
        Bundle bundle = data.getExtras();
        // from bundle get the image, so we can do something with it if we want
        Bitmap bitmap = (Bitmap) bundle.get("data");
        // For setting the image in image view or save this information somewhere for the user to view

        // ======= PROCESS IMAGE ====== //
        // create FirebaseVisionImage Object from a Bitmap object
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        // get an instance of firebase vision
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        // create an instance of FirebaseVisionTextRecognizer (this actually recognises the text)
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getCloudTextRecognizer();
        // create a task to process the image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        // if task is successful, provide a response to the user
        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String extractedText = firebaseVisionText.getText();
                Toast.makeText(MapsActivity.this, "Captured text: " + extractedText, Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this,
                        "Failed to capture text: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    // ====== ON CLICK LISTENER FOR CAMERA ICON ===== //
    public void cameraProcess(View view) {
        // Check user permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
        // If permission has been granted open the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Leave the camera view
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
}