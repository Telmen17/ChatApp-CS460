/**
 * An Adapter class that prepares the chat messages to display in the RecyclerView.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp_cs460.databinding.ItemContainerReceivedMessageBinding;
import com.example.chatapp_cs460.databinding.ItemContainerSentMessageBinding;
import com.example.chatapp_cs460.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    /**
     * Info and constant variable fields.
     */
    private Bitmap receiverProfileImage;
    private final List<ChatMessage> chatMessages;
    private final String sendId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    /**
     * Constructor for the ChatAdapter.
     * @param receiverProfileImage Bitmap profile image.
     * @param chatMessages List of chat messaages.
     * @param sendId String type of sender ID.
     */

    public ChatAdapter(Bitmap receiverProfileImage, List<ChatMessage> chatMessages, String sendId) {
        this.receiverProfileImage = receiverProfileImage;
        this.chatMessages = chatMessages;
        this.sendId = sendId;
    }

    /**
     * Overridden method that creates view holder depending on the sender and receiver.
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return Returns sender or receiver ViewHolder depending on message type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // If type is sent, inflate sending layout
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            // Else when type is received, inflate receiving message layout
            return new ReceierMessageViewHolder(ItemContainerReceivedMessageBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    /**
     * Method that binds the data to the ViewHolders.
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // If sent type, connect data
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder)
                    .setData(chatMessages.get(position));
        } else {
            // Else when received type, connect messages and profile image
            ((ReceierMessageViewHolder) holder)
                    .setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    /**
     * An overridden method to determine the size of items in the RecyclerView.
     * @return The size of the chatMessages List.
     */
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    /**
     * Method to get item view type of send or received.
     * @param position position to query
     * @return Returns an integer corresponding to sent or received message.
     */
    @Override
    public int getItemViewType(int position) {
        // Sent type
        if (chatMessages.get(position).senderId.equals(sendId)) {
            return VIEW_TYPE_SENT;
        } else {
            // Received type
            return VIEW_TYPE_RECEIVED;
        }
    }


    /**
     * Sub class sending message ViewHolder.
     */
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        /**
         * Binder field.
         */
        private final ItemContainerSentMessageBinding binding;

        /**
         * Send message ViewHolder constructor.
         * @param itemContainerSentMessageBinding SentMessage layout binder.
         */
        public SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        /**
         * Method to set data to the bound layouts.
         * @param chatMessage ChatMessage object class variable.
         */
        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    /**
     * Sub class for received message ViewHolder.
     */
    static class ReceierMessageViewHolder extends RecyclerView.ViewHolder {

        /**
         * Binder field.
         */
        private final ItemContainerReceivedMessageBinding binding;

        /**
         * Received message ViewHolder constructor.
         * @param itemContainerReceivedMessageBinding Received message binding with layout.
         */
        public ReceierMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        /**
         * Helper method for received message to set the message data.
         * @param chatMessage ChatMessage class object containing message details.
         * @param receiverProfileImage Bitmap profile image.
         */
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            // Connect data through binder
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            // Bind profile image
            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }
        }
    }
}
