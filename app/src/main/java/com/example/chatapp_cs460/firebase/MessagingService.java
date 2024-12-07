/**
 * A messaging service utility class. Lies dormant.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    /**
     * Action listener that creates a new token and logs it.
     * @param token The token used for sending messages to this application instance. This token is
     *     the same as the one retrieved by.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token:" + token);
    }

    /**
     * An action listener that reacts when message is received and logs it.
     * @param message Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCM", "460 Message:" + message.getNotification().getBody());
    }
}
