/**
 * Activity class that controls the processes in the chat messaging page.
 */
package com.example.chatapp_cs460.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.example.chatapp_cs460.adapters.ChatAdapter;
import com.example.chatapp_cs460.databinding.ActivityChatBinding;
import com.example.chatapp_cs460.models.ChatMessage;
import com.example.chatapp_cs460.models.User;
import com.example.chatapp_cs460.utilities.Constants;
import com.example.chatapp_cs460.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    /**
     * ChatActivity relevant fields of binding, user, messages, preferences, and database.
     */
    private ActivityChatBinding binding;

    private User receiverUser;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    /**
     * Method that creates the elements and processes in the Chat Activity.
     * @param savedInstanceState Saved instance state of the application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind setup
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        // View setup
        setContentView(binding.getRoot());
        // Receiver data fetch
        loadReceiverDetails();
        // Setting up listeners
        setListeners();
        // Initialising components
        init();
        // Fetching messages from the database
        ListenMessage();

    }

    /**
     * Method that initialises the activity fields
     */
    private void init() {
        // Connecting fields
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                getBitmapFromEncodedString(receiverUser.image),
                chatMessages,
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        // Connecting adapter to RecyclerView
        binding.chatRecyclerView.setAdapter(chatAdapter);
        // Connecting database instance
        database = FirebaseFirestore.getInstance();
    }

    /**
     * Send message to the database.
     */
    private void sendMessages() {
        HashMap<String, Object> message = new HashMap<>();

        // Adding pair values
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());

        // Adding into database
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        // Resetting input field
        binding.inputMessage.setText(null);
    }

    /**
     * Method that listens and fetches messages from the database.
     */
    private void ListenMessage() {
        // Fetching where sender is the main user
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,
                    preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);

        // Fetching where the sender is the opposite user
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,
                        receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    /**
     * EventLister from the database where chat messages are dynamically refereshed.
     */
    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        // If value exists
        if (value != null) {
            int count = chatMessages.size();
            // For every document change
            for (DocumentChange documentChange:value.getDocumentChanges()) {
                // If changed message type is added
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    // Create new chatMessage
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(
                            documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));

                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    // Add to chatMessages
                    chatMessages.add(chatMessage);
                }
            }
            // Sorting the chat messages
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            // If count is 0
            if (count == 0) {
                // Notify change
                chatAdapter.notifyDataSetChanged();
            } else {
                // Else, notify updated changes
                chatAdapter.notifyItemRangeChanged(chatMessages.size(), chatMessages.size());

                // Scroll to add new message
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            // Make RecyclerView visible
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        // Remove Progress Bar from page
        binding.progressBar.setVisibility(View.GONE);
    });

    /**
     * Method that decodes the profileImage String from the database.
     * @param encodedImage String type encoded profile image.
     * @return Returns a Bitmap profile image for easy process.
     */
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        // Turning into byte array.
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        // Returning Bitmap
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Method to load receiver details of the chat conversation.
     */
    private void loadReceiverDetails() {
        // Fetching opposite user from the database
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        // Setting opposite user name
        binding.textName.setText(receiverUser.name);

    }

    /**
     * Method that sets listeners for clicking.
     */
    private void setListeners() {
        // Back icon press
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        // Send icon press
        binding.layoutSend.setOnClickListener(v -> sendMessages());
    }

    /**
     * Method that turns a Data object into a String date format.
     *  @param date Data class type object.
     * @return Returns a formatted date in String type.
     */
    private String getReadableDateTime(Date date) {
        // Turning Date object into formatted String
        return new SimpleDateFormat("MMM dd, yyyy - hh:mm a",
                Locale.getDefault()).format(date);
    }
}