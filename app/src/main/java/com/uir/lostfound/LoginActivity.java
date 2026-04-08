package com.uir.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.uir.lostfound.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * LoginActivity is the app entry screen.
 * - ask the user for student ID and name
 * - validate the inputs
 * - save the session using SessionManager
 * - redirect the user to ItemFeedActivity
 *
 * If the user is already logged in, it skips the form
 * and goes directly to the feed.
 *
 * Ownership : Omar
 */

public class LoginActivity extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etName;
    private Button btnLogin;

    private SessionManager sessionManager;

    /** Registered users: student ID → full name. */
    private static final Map<String, String> VALID_USERS = new HashMap<>();
    static {
        VALID_USERS.put("STU001", "Hasnae Ghiyati");
        VALID_USERS.put("STU002", "Fatima Zahra");
        VALID_USERS.put("STU003", "Youssef Benali");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create the session manager for this activity
        sessionManager = new SessionManager(this);

        // If user is already logged in, skip login screen
        if (sessionManager.isLoggedIn()) {
            goToItemFeed();
            return;
        }

        etStudentId = findViewById(R.id.etStudentId);
        etName = findViewById(R.id.etName);
        btnLogin = findViewById(R.id.btnLogin);

        // When button is clicked, try to log in
        btnLogin.setOnClickListener(view -> attemptLogin());
    }

    /**
     * Reads input fields, validates them,
     * saves the user session, then opens the feed screen.
     */
    private void attemptLogin() {
        String studentId = etStudentId.getText().toString().trim();
        String name = etName.getText().toString().trim();

        boolean hasError = false;

        if (TextUtils.isEmpty(studentId)) {
            etStudentId.setError(getString(R.string.error_student_id_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.error_name_required));
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Validate credentials against registered users
        String expectedName = VALID_USERS.get(studentId);
        if (expectedName == null || !expectedName.equalsIgnoreCase(name)) {
            Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show();
            etStudentId.setError(getString(R.string.error_invalid_credentials));
            etName.setError(getString(R.string.error_invalid_credentials));
            return;
        }

        // Save user data locally
        sessionManager.saveUser(studentId, expectedName);

        Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();

        goToItemFeed();
    }

    /**
     * Opens ItemFeedActivity and closes LoginActivity.
     */
    private void goToItemFeed() {
        Intent intent = new Intent(LoginActivity.this, ItemFeedActivity.class);
        startActivity(intent);
        finish();
    }
}