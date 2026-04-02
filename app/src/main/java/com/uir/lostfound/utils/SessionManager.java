package com.uir.lostfound.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "campus_lost_found_prefs";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_NAME = "name";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

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
        return getStudentId() != null && getName() != null;
    }

    public void clearUser() {
        editor.clear();
        editor.apply();
    }
}