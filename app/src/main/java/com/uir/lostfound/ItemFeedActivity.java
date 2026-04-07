package com.uir.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uir.lostfound.adapter.ItemFeedAdapter;
import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.model.LostItem;
import com.uir.lostfound.utils.SessionManager;

import io.realm.RealmResults;

/**
 * ItemFeedActivity
 *
 * OWNERSHIP SPLIT:
 * - Idriss: RecyclerView + Realm data + Adapter
 * - Omar : session control + navigation guard
 */
public class ItemFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemFeedAdapter adapter;
    private RealmHelper realmHelper;

    //adding login (Omar)
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Omar :
        // Initialize session
        sessionManager = new SessionManager(this);

        // Block access if not logged in
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        //Idriss :
        setContentView(R.layout.activity_item_feed);
        try {
            realmHelper = RealmHelper.getInstance();
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            RealmResults<LostItem> items = realmHelper.getAllItems();
            adapter = new ItemFeedAdapter(this, items);
            recyclerView.setAdapter(adapter);

            Toast.makeText(this, "Items trouvés : " + items.size(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Omar :
     * Handles navigation if user is not authenticated
     */
    private void redirectToLogin() {
        Intent intent = new Intent(ItemFeedActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realmHelper != null) {
            realmHelper.close();
        }
    }
}