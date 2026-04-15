package com.uir.lostfound;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
//import com.uir.lostfound.databinding.ActivityMainBinding;

/**
 * MainActivity — Phase 1 skeleton entry point.
 *
 * This Activity is kept for reference but is no longer the LAUNCHER.
 * LoginActivity is the actual launcher; this class immediately forwards
 * to ItemFeedActivity and finishes itself.
 *
 * Safe to remove in a future cleanup pass.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //ActivityMainBinding binding;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        /*
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.tvWelcome.setText("Campus Lost & Found\nPhase 1 skeleton ready");
        */

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lancer ItemFeedActivity au démarrage
        Intent intent = new Intent(this, ItemFeedActivity.class);
        startActivity(intent);
        finish();
    }
}