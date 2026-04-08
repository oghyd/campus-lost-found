package com.uir.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
 * - Omar : session control + navigation guard + ActionBar menu wiring
 */
public class ItemFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
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

        try {
            realmHelper = RealmHelper.getInstance();
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            RealmResults<LostItem> items = realmHelper.getAllItems();
            adapter = new ItemFeedAdapter(this, items);
            recyclerView.setAdapter(adapter);
            findViewById(R.id.fab_add).setOnClickListener(v ->
                    startActivity(new Intent(this, PostItemActivity.class))
            );
            adapter.setOnItemClickListener(item -> {
                Intent intent = new Intent(this, ItemDetailActivity.class);
                intent.putExtra("item_id", item.getId());
                startActivity(intent);
            });
            SearchView searchView = findViewById(R.id.searchView);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.filter(query);
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.filter(newText);
                    return true;
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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
            Toast.makeText(this, "Showing LOST items", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemId == R.id.action_filter_found) {
            adapter.filterByType("FOUND");
            Toast.makeText(this, "Showing FOUND items", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (itemId == R.id.action_filter_all) {
            adapter.filterByType(null);
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

        if (itemId == R.id.action_my_reports) {
            startActivity(new Intent(this, MyPostsActivity.class));
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