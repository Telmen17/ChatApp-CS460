/**
 * A Java class to control the String, Boolean pair preference configurations of the app.
 * @author Telmen Enkhtuvshin
 */
package com.example.chatapp_cs460.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    /**
     * SharedPreferences field.
     */
    private final SharedPreferences sharedPreferences;

    /**
     * A constructor for the PreferenceManager class. Connects values to the field.
     * @param context Context class object that retrieves the context of the app.
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    /**
     * A method that inserts String, Boolean preference pair into SharedPreferences.
     * @param key String type key to be inserted.
     * @param value Boolean type value to be inserted with key.
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * A method that gets the Boolean value of a preference key.
     * @param key String type key to be used.
     * @return Returns a Boolean value of the corresponding key of the preference.
     */
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * A method that inserts the String, String pair preferences.
     * @param key String type key element of a preference.
     * @param value String type value that corresponds to the key of the preference.
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * A method to get the String value of the sharedPreferences object.
     * @param key The key to be used to retrieve the String value.
     * @return Returns the String value of the corresponding key.
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    /**
     * A method to clear the preference information.
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
