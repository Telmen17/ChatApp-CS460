/**
 * A class that controls the backend activities of the Sign Up page.
 * Interacts send user information to the database and updates the view accordingly.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp_cs460.R;
import com.example.chatapp_cs460.databinding.ActivitySignUpBinding;
import com.example.chatapp_cs460.utilities.Constants;
import com.example.chatapp_cs460.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    /**
     * Class fields
     */
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;

    private String encodeImage;

    /**
     * An overridden method that initializes the objects and connects values to the fields.
     * @param savedInstanceState Instance state of the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }

    /**
     * A helper method that sets up listeners to the actions that the user would do.
     */
    private void setListeners() {
        // Sign In button clicked, turn back the page
        binding.textSignIn.setOnClickListener(v -> onBackPressed());

        // If Sign up button is pressed, check credentials and signup.
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {
                Signup();
            }
        });

        // If the image field is clicked, allow the mobile phone to access image media to use
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
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
     * A function that interacts with the Firebase database and executes the sign up process
     */
    private void Signup() {
        // Check loading
        loading(true);
        // Post to Firebase
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();

        // Putting user info
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());

        user.put(Constants.KEY_IMAGE, encodeImage);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                // Database write successful
                .addOnSuccessListener(documentReference -> {
                    loading(false);

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodeImage);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                })
                // Database write not successful
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });

    }

    /**
     * A helper function that turns a Bitmap image into byte array stream to store in the database.
     * @param bitmap A Bitmap class image.
     * @return Returns a String that is derived from an image.
     */
    private String encodeImage(Bitmap bitmap) {
        // Fixing scale
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth / bitmap.getWidth();

        // Turning into bitmap
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Compressing into output stream
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        // Putting it into byte array
        byte[] bytes = byteArrayOutputStream.toByteArray();

        // Returning the final String
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * A helper function that controls the processes of the Profile Image view element.
     * Decodes the image and displays it into the Profile Image field.
     */
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // If the result is successful
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Accepting input stream
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Setting the image into the GUI elements through view binding
                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodeImage = encodeImage(bitmap);

                    } catch (FileNotFoundException e) {
                        // Catch error
                        e.printStackTrace();
                    }
                }
            }
    );

    /**
     * Validates credentials format
     * @return Boolean type true if credentials are good, false if not.
     */
    private Boolean isValidSignUpDetails() {
        if (encodeImage == null) {
            showToast("Please select your image");
            return false;
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your Name");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please enter your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please enter a valid Email");
            return false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please enter your Password");
            return false;
        } else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Please confirm your Password");
            return false;
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Password & Confirm Password must match");
            return false;
        } else {
            return true;
        }
    }

    /**
     * A helper function that controls whether to show the sign up button or the progressBar
     * @param isLoading Boolean type loading indicator.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}