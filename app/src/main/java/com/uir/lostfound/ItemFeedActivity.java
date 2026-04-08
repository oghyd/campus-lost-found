package com.uir.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
 * - Omar : session control + navigation guard + ActionBar menu wiring
 */
public class ItemFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ItemFeedAdapter adapter;
    private RealmHelper realmHelper;
    private SessionManager sessionManager;
    private boolean sortNewestFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_item_feed);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvEmpty = findViewById(R.id.tv_empty);

        // FAB to post new item
        FloatingActionButton fab = findViewById(R.id.fab_post);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, PostItemActivity.class)));

        // Search
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                updateEmptyState();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                updateEmptyState();
                return true;
            }
        });

        try {
            realmHelper = RealmHelper.getInstance();
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            loadItems();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loadItems() {
        RealmResults<LostItem> items = realmHelper.getAllItems();
        adapter = new ItemFeedAdapter(this, items);
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra("item_id", item.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (realmHelper != null) {
            loadItems();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_filter_lost) {
            adapter.filterByType("LOST");
            updateEmptyState();
            Toast.makeText(this, "Showing LOST items", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemId == R.id.action_filter_found) {
            adapter.filterByType("FOUND");
            updateEmptyState();
            Toast.makeText(this, "Showing FOUND items", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemId == R.id.action_filter_all) {
            adapter.filterByType(null);
            updateEmptyState();
            Toast.makeText(this, "Showing ALL items", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemId == R.id.action_sort_date) {
            sortNewestFirst = !sortNewestFirst;
            adapter.sortByDate(sortNewestFirst);
            Toast.makeText(this,
                    sortNewestFirst ? "Newest first" : "Oldest first",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemId == R.id.action_logout) {
            sessionManager.clearUser();
            redirectToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
