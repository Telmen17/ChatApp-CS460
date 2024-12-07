/**
 * An adapter class that lists the available users to chat with in a RecyclerView.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp_cs460.databinding.ItemContainerUserBinding;
import com.example.chatapp_cs460.listeners.UserListener;
import com.example.chatapp_cs460.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    /**
     * UsersAdapter fields tha contain list of users and user listener.
     */
    private final List<User> users;
    private final UserListener userListener;

    /**
     * Constructor for the UsersAdapter class.
     * @param users List of users.
     * @param userListener User listener interface that contain action listener function.
     */

    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    /**
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return Returns a UserViewHolder that has an inflated layout.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // User container layout inflation from binder
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new UserViewHolder(itemContainerUserBinding);
    }

    /**
     *  A action listener method that binds users to the ViewHolder.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    /**
     *  RecyclerView's Adaptors size method.
     * @return Returns the users list size to determine the RecyclerView size.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     *  A sub UserViewHolder class that holds User information and layout.
     */
    class UserViewHolder extends RecyclerView.ViewHolder {
        /**
         * Field of binding.
         */
        ItemContainerUserBinding binding;

        /**
         * Constructor for the Holder
         * @param itemContainerUserBinding User layout binding.
         */
        public UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        /**
         * A method that sets the User data in the binded layout.
         * @param user User class individual object.
         */
        void setUserData(User user) {
            // Connecting user data
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));

            // Setting action listener
            binding.getRoot().setOnClickListener(v -> {
                userListener.onUserClicked(user);
            });
        }
    }

    /**
     * A method that decodes the user profile image.
     * @param encodedImage String of image information.
     * @return Returns a Bitmap of user profile image.
     */
    private Bitmap getUserImage(String encodedImage) {
        // Turning into byte array
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);

        // Producing Bitmap
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
