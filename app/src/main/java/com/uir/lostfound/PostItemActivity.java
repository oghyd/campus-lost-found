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
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.uir.lostfound.utils.ImageHelper;
import java.io.File;
import java.io.IOException;


/**
 * PostItemActivity — form for creating or editing a lost/found report.
 *
 * Responsibilities:
 * - Collect title, description, location, category, type (LOST/FOUND), and date.
 * - Optionally attach a photo via the camera (Bonus 1).
 * - When launched with ITEM_ID extra, pre-fills the form for editing an existing item.
 * - Persists the item through RealmHelper.
 *
 * Ownership: Ines (form + camera); Omar wires SessionManager for owner fields.
 */
public class PostItemActivity extends AppCompatActivity {

    // UI fields
    private TextInputEditText etTitle, etDescription, etLocation;
    private Spinner spinnerCategory;
    private MaterialButtonToggleGroup toggleType;
    private MaterialButton btnDate, btnSubmit, btnAttachPhoto;
    private android.widget.ImageView ivPhotoPreview;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_CAMERA_PERMISSION = 102;
    private Uri photoUri;

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
        String editItemId = getIntent().getStringExtra("ITEM_ID");
        if (editItemId != null) {
            prefillFormForEdit(editItemId);
            btnSubmit.setText("Save Changes");
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Edit Item");
        }
        // Phase 4: camera — wired in Phase 4
        btnAttachPhoto.setOnClickListener(v -> openCamera());
    }
    /** Checks camera permission before launching the camera intent. */
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        launchCameraIntent();
    }

    /** Creates a temp file via ImageHelper, then fires ACTION_IMAGE_CAPTURE with a FileProvider URI. */
    private void launchCameraIntent() {
        try {
            File photoFile = ImageHelper.createImageFile(this);
            photoUri = FileProvider.getUriForFile(
                    this,
                    "com.uir.lostfound.fileprovider",
                    photoFile
            );
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCameraIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Load the full-size photo from the URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), photoUri);
                // Save to internal storage and get path
                selectedPhotoPath = ImageHelper.saveImageToInternalStorage(this, bitmap);
                // Show preview
                ivPhotoPreview.setImageBitmap(bitmap);
                ivPhotoPreview.setVisibility(android.view.View.VISIBLE);
                btnAttachPhoto.setText("Change Photo");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load photo", Toast.LENGTH_SHORT).show();
            }
        }
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
            item.setOwnerName(session.getName());
            item.setPhotoPath(selectedPhotoPath); // null if no photo

            // Save to Realm
            RealmHelper.getInstance().insertItem(item);

            Toast.makeText(this, "Item posted!", Toast.LENGTH_SHORT).show();
            finish(); // go back to feed
        });
    }

    /**
     * Pre-fills every form field with the existing values of the item identified by {@code itemId}.
     * Also rewires the submit button to call {@link RealmHelper#updateItem} instead of insert.
     */
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

        // Load existing photo if any
        if (item.getPhotoPath() != null) {
            selectedPhotoPath = item.getPhotoPath();
            try {
                android.graphics.BitmapFactory.Options opts = new android.graphics.BitmapFactory.Options();
                opts.inSampleSize = 2;
                android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeFile(item.getPhotoPath(), opts);
                if (bmp != null) {
                    ivPhotoPreview.setImageBitmap(bmp);
                    ivPhotoPreview.setVisibility(View.VISIBLE);
                    btnAttachPhoto.setText(R.string.change_photo);
                }
            } catch (Exception ignored) {}
        }

        // Wire submit to update instead of insert
        btnSubmit.setOnClickListener(v -> {
            if (!validateForm()) return;

            String title    = etTitle.getText().toString().trim();
            String desc     = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String type     = (toggleType.getCheckedButtonId() == R.id.btn_lost)
                    ? "LOST" : "FOUND";

            LostItem updated = new LostItem();
            updated.setTitle(title);
            updated.setDescription(desc);
            updated.setLocation(location);
            updated.setCategory(category);
            updated.setType(type);
            updated.setTimestamp(selectedDateMillis);
            updated.setPhotoPath(selectedPhotoPath);

            RealmHelper.getInstance().updateItem(itemId, updated);

            Toast.makeText(this, R.string.edit_saved, Toast.LENGTH_SHORT).show();
            finish();
        });
    }



    // ── Validation ────────────────────────────────────────────
    /**
     * Validates all required fields. Sets inline errors on EditTexts and shows
     * Toast messages for toggle/date errors.
     *
     * @return true if all fields are filled correctly, false otherwise.
     */
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
