/**
 * A Java class that controls the Sign In processes of the application.
 * GUI pages are updated accordingly.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp_cs460.R;
import com.example.chatapp_cs460.databinding.ActivitySignInBinding;
import com.example.chatapp_cs460.utilities.Constants;
import com.example.chatapp_cs460.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    /**
     * Fields of the class
     */
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;


    /**
     * An overridden method that initializes the class elements
     * @param savedInstanceState Initial state of the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    /**
     * A helper method that sets the user action listeners.
     */
    private void setListeners() {
        // If Create New Account button is clicked, change page.
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        // If Sign In button is clicked, check credentials and sign in.
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                SignIn();
                showToast("Sign In Successful");
            }
        });
    }

    /**
     * A helper function that displays toast messages to the page.
     * @param message A String type message to display.
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * A method that controls the sign in process with the database.
     * Compares user input with data in the database.
     */
    private void SignIn() {
        // Displaying loading bar while the credentials are validated
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                // Comparing inputs with the database
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                // If validation is completed, get the result of tasks and set preferences
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        // Getting document snapshot of task result
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        // Setting preferences
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));

                        // Changing pages with an Intent
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Makes the progress bar disappear and shows error Toast message
                        loading(false);
                        showToast("Unable to Sign In");
                    }
                });
    }

    /**
     * A helper function that controls the Sign in button and ProgressBar visibility depending
     * on the loading state.
     * @param isLoading Boolean type value that indicates loading or not.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * A method that checks whether the input fields are correctly filled in.
     * @return Returns Boolean type of true if valid, else returns false.
     */
    private boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please enter your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please enter a valid Email");
            return false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please enter your Password");
            return false;
        } else {
            return true;
        }
    }
}