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
import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.model.LostItem;
import com.uir.lostfound.utils.SessionManager;
import java.util.UUID;

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

            // Read values from form
            String title    = etTitle.getText().toString().trim();
            String desc     = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String type     = (toggleType.getCheckedButtonId() == R.id.btn_lost)
                    ? "LOST" : "FOUND";

            // Get logged-in user from SessionManager
            SessionManager session = new SessionManager(this);

            // Build the LostItem
            LostItem item = new LostItem();
            item.setId(UUID.randomUUID().toString());
            item.setTitle(title);
            item.setDescription(desc);
            item.setLocation(location);
            item.setCategory(category);
            item.setType(type);
            item.setStatus("OPEN");
            item.setTimestamp(selectedDateMillis);
            item.setOwnerStudentId(session.getStudentId());
            item.setOwnerName(session.getUserName());
            item.setPhotoPath(selectedPhotoPath); // null if no photo

            // Save to Realm
            RealmHelper.getInstance().insertItem(item);

            Toast.makeText(this, "Item posted!", Toast.LENGTH_SHORT).show();
            finish(); // go back to feed
        });
    }
    String editItemId = getIntent().getStringExtra("ITEM_ID");
    if (editItemId != null) {
        prefillFormForEdit(editItemId);
        btnSubmit.setText("Save Changes");
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Edit Item");
    }
    private void prefillFormForEdit(String itemId) {
        LostItem item = RealmHelper.getInstance().getItemById(itemId);
        if (item == null) return;

        etTitle.setText(item.getTitle());
        etDescription.setText(item.getDescription());
        etLocation.setText(item.getLocation());

        // Set Spinner to saved category
        String[] cats = CATEGORIES;
        for (int i = 0; i < cats.length; i++) {
            if (cats[i].equals(item.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // Set toggle
        if ("LOST".equals(item.getType()))
            toggleType.check(R.id.btn_lost);
        else
            toggleType.check(R.id.btn_found);

        // Set date
        selectedDateMillis = item.getTimestamp();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy",
                java.util.Locale.getDefault());
        btnDate.setText("Date: " + sdf.format(new java.util.Date(selectedDateMillis)));

        // Wire submit to update instead of insert
        btnSubmit.setOnClickListener(v -> {
            if (!validateForm()) return;
            // TODO: call RealmHelper.updateItem(itemId, ...) when Idriss adds that method
            Toast.makeText(this, "Edit saved!", Toast.LENGTH_SHORT).show();
            finish();
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
