package com.uir.lostfound;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class PostItemActivity extends AppCompatActivity {

    // UI fields
    private TextInputEditText etTitle, etDescription, etLocation;
    private Spinner spinnerCategory;
    private MaterialButtonToggleGroup toggleType;
    private MaterialButton btnDate, btnSubmit, btnAttachPhoto;
    private android.widget.ImageView ivPhotoPreview;

    // State
    private long selectedDateMillis = 0;
    private String selectedPhotoPath = null; // used in Phase 4

    // Category options
    private final String[] CATEGORIES = {
            "Electronics", "Documents", "Clothing", "Bags", "Keys", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        // Set toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post Item");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        etTitle        = findViewById(R.id.et_title);
        etDescription  = findViewById(R.id.et_description);
        etLocation     = findViewById(R.id.et_location);
        spinnerCategory = findViewById(R.id.spinner_category);
        toggleType     = findViewById(R.id.toggle_type);
        btnDate        = findViewById(R.id.btn_date);
        btnSubmit      = findViewById(R.id.btn_submit);
        btnAttachPhoto = findViewById(R.id.btn_attach_photo);
        ivPhotoPreview = findViewById(R.id.iv_photo_preview);

        setupCategorySpinner();
        setupDatePicker();
        setupSubmitButton();

        // Phase 4: camera — wired in Phase 4
        btnAttachPhoto.setOnClickListener(v ->
                Toast.makeText(this, "Camera coming in Phase 4", Toast.LENGTH_SHORT).show()
        );
    }

    // ── Spinner ──────────────────────────────────────────────
    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_category_item,
                CATEGORIES
        );
        adapter.setDropDownViewResource(R.layout.spinner_category_item);
        spinnerCategory.setAdapter(adapter);
    }

    // ── DatePicker ────────────────────────────────────────────
    private void setupDatePicker() {
        btnDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, day);
                        selectedDateMillis = selected.getTimeInMillis();
                        String label = day + "/" + (month + 1) + "/" + year;
                        btnDate.setText("Date: " + label);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    // ── Submit ────────────────────────────────────────────────
    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            if (!validateForm()) return;
            // TODO Phase 3: save to Realm here
            Toast.makeText(this, "Form valid — Realm save coming in Phase 3", Toast.LENGTH_SHORT).show();
        });
    }

    // ── Validation ────────────────────────────────────────────
    private boolean validateForm() {
        boolean valid = true;

        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String desc  = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String loc   = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            valid = false;
        }
        if (desc.isEmpty()) {
            etDescription.setError("Description is required");
            valid = false;
        }
        if (loc.isEmpty()) {
            etLocation.setError("Location is required");
            valid = false;
        }
        if (toggleType.getCheckedButtonId() == View.NO_ID) {
            Toast.makeText(this, "Please select Lost or Found", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (selectedDateMillis == 0) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    // ── Back button ───────────────────────────────────────────
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
