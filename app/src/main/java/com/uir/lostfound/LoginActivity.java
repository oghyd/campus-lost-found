package com.uir.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.uir.lostfound.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilStudentId;
    private TextInputLayout tilName;
    private TextInputEditText etStudentId;
    private TextInputEditText etName;
    private MaterialButton btnLogin;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Skip login screen if a session already exists
        if (sessionManager.isLoggedIn()) {
            navigateToFeed();
            return;
        }

        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tilStudentId = findViewById(R.id.til_student_id);
        tilName      = findViewById(R.id.til_name);
        etStudentId  = findViewById(R.id.et_student_id);
        etName       = findViewById(R.id.et_name);
        btnLogin     = findViewById(R.id.btn_login);

        etName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        tilStudentId.setError(null);
        tilName.setError(null);

        String studentId = etStudentId.getText() != null
                ? etStudentId.getText().toString().trim() : "";
        String name = etName.getText() != null
                ? etName.getText().toString().trim() : "";

        boolean valid = true;

        if (studentId.isEmpty()) {
            tilStudentId.setError(getString(R.string.error_student_id_required));
            valid = false;
        }
        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_name_required));
            valid = false;
        }

        if (!valid) return;

        sessionManager.saveUser(studentId, name);
        navigateToFeed();
    }

    private void navigateToFeed() {
        startActivity(new Intent(this, ItemFeedActivity.class));
        finish();
    }
}
