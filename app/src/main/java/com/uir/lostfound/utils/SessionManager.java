package com.uir.lostfound.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager is responsible for storing and retrieving
 * the current user's session data using SharedPreferences.
 *
 * In this project, the session contains:
 * - student ID
 * - user name
 *
 * This lets the app remember the user even after the app closes.
 * Ownership : Omar
 */
public class SessionManager {

    // Name of the SharedPreferences file stored on the device
    private static final String PREF_NAME = "campus_lost_found_prefs";

    // Keys used to store values inside SharedPreferences
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_NAME = "name";

    // SharedPreferences object used for reading saved values
    private final SharedPreferences preferences;

    // Editor object used for writing values
    private final SharedPreferences.Editor editor;

    //Constructor
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    //Saves current user data in SharedPreferences
    public void saveUser(String studentId, String name) {
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public String getStudentId() {
        return preferences.getString(KEY_STUDENT_ID, null);
    }

    public String getName() {
        return preferences.getString(KEY_NAME, null);
    }

    public boolean isLoggedIn() {
        return getStudentId() != null && !getStudentId().trim().isEmpty() && getName() != null && !getName().trim().isEmpty();
    }

    public void clearUser() {
        editor.clear();
        editor.apply();
    }
}