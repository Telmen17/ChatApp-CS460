/**
 * An interface class that contains an action listener
 * that reacts when the user profile is clicked.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.listeners;

import com.example.chatapp_cs460.models.User;

public interface UserListener {
    /**
     * Action listener that activates when the user clicks on the user profile.
     * @param user
     */
    void onUserClicked(User user);
}
