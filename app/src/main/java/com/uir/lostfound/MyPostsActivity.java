package com.uir.lostfound;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uir.lostfound.adapter.MyPostsAdapter;
import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.model.LostItem;
import com.uir.lostfound.utils.SessionManager;
import io.realm.RealmResults;

public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private MyPostsAdapter adapter;
    private RealmHelper realmHelper;
    private String currentStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        // Use SessionManager (single source of truth) instead of raw SharedPreferences
        SessionManager session = new SessionManager(this);
        currentStudentId = session.getStudentId();

        realmHelper = RealmHelper.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_posts_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tv_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMyPosts();

        adapter.setOnItemClickListener(new MyPostsAdapter.OnItemClickListener() {
            @Override
            public void onEdit(LostItem item) {
                Intent intent = new Intent(MyPostsActivity.this, PostItemActivity.class);
                intent.putExtra("ITEM_ID", item.getId());
                intent.putExtra("EDIT_MODE", true);
                startActivity(intent);
            }

            @Override
            public void onDelete(LostItem item) {
                new AlertDialog.Builder(MyPostsActivity.this)
                        .setTitle(R.string.delete_confirm_title)
                        .setMessage(R.string.delete_confirm_message)
                        .setPositiveButton(R.string.delete_confirm_yes, (dialog, which) -> {
                            realmHelper.deleteItem(item.getId());
                            loadMyPosts();
                            Toast.makeText(MyPostsActivity.this,
                                    R.string.post_deleted, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
    }

    private void loadMyPosts() {
        RealmResults<LostItem> myItems = realmHelper.getItemsByOwner(currentStudentId);
        adapter = new MyPostsAdapter(this, myItems);
        recyclerView.setAdapter(adapter);

        // Toggle empty state
        if (myItems.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyPosts();
    }
}
