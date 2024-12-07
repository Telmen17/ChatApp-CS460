/**
 * An activity class that controls the overall users to chat with. Users are listed in RecyclerView.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp_cs460.adapters.UsersAdapter;
import com.example.chatapp_cs460.databinding.ActivityUserBinding;
import com.example.chatapp_cs460.listeners.UserListener;
import com.example.chatapp_cs460.models.User;
import com.example.chatapp_cs460.utilities.Constants;
import com.example.chatapp_cs460.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements UserListener {

    /**
     * Binding and preferenceManager fields.
     */
    private ActivityUserBinding binding;
    private PreferenceManager preferenceManager;

    /**
     * Action listener method that creates the elements in the activity and sets up the processes.
     * @param savedInstanceState Saved state of the page.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Connecting fields
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        // Setting view
        setContentView(binding.getRoot());
        // Setting listener and fetching users from the database
        setListeners();
        getUsers();
    }

    /**
     * A method that sets listeners of clicking the back icon.
     */
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    /**
     * A method that fetches user data from the database to display in the page.
     */
    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // Fetching information from the database
        database.collection(Constants.KEY_COLLECTION_USERS).get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Creating users list
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            // Creating individual user and connecting the info
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        // Connecting the adapter to the Recyclerview with the list of users
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    /**
     * A helper function that sets the error message to TextView
     */
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * A method that controls the Progress bar to show up and disappear.
     * @param isLoading Boolean value if the app is loading or not.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * An action listener method that reacts when user is clicked. A new page is activated
     * wtih an intent.
     * @param user User class object.
     */
    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}