package com.uir.lostfound;

import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uir.lostfound.adapter.ItemFeedAdapter;
import com.uir.lostfound.db.DataSeeder;
import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.model.LostItem;
import io.realm.RealmResults;

public class ItemFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemFeedAdapter adapter;
    private SearchView searchView;
    private RealmHelper realmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_feed);

        // Initialiser RealmHelper
        realmHelper = RealmHelper.getInstance();

        // Ajouter des données de test
        DataSeeder.seedData(realmHelper);

        // Configurer Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Campus Lost & Found");

        // Configurer RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Charger les données
        loadItems();

        // Configurer SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void loadItems() {
        RealmResults<LostItem> items = realmHelper.getAllItems();
        adapter = new ItemFeedAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmHelper.close();
    }
}