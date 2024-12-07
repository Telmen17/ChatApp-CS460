/**
 * MainActivity class that controls the chat messaging functionalities and pages.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp_cs460.databinding.ActivityMainBinding;
import com.example.chatapp_cs460.utilities.Constants;
import com.example.chatapp_cs460.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    /**
     * Main Activity fields.
     */
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;


    /**
     * An overridden method that creates the app elements and processes.
     * @param savedInstanceState An instance state of the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
    }

    /**
     * Setting up listeners for clicking.
     */
    private void setListeners() {
        // Sign out icon listener
        binding.imagesSignOut.setOnClickListener(v -> signOut());
        // Create new chat icon listener
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UserActivity.class)));
    }

    /**
     * Method to load main user details in the page.
     */
    private void loadUserDetails() {
        // Name
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        // Profile image
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        //Token
        getToken();
    }

    /**
     * A helper function that displays toast messages on the app page.
     * @param message A String message to display on the page.
     */
    private void showToast(String message) {
        // Showing Toast Message
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method that gets the activity token from the database.
     */
    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    /**
     * A method that updates the token in the database.
     * @param token String type token.
     */
    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // Users collection main user ID document
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        // Updating token in the database
        documentReference.update(Constants.KEY_FCM_TOKEN, token).addOnSuccessListener(unused ->
                showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to update Token"));
    }

    /**
     * Method that signs out the user from the application.
     */
    private void signOut() {
        showToast("Signing out ...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // Main User document
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        // Deleting token from database
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        // Clearing preferences and starting new sign in activity
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }).addOnFailureListener(e -> showToast("Unable to sign out"));
    }
}