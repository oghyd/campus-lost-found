package com.uir.lostfound;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uir.lostfound.adapter.MyPostsAdapter;
import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.model.LostItem;
import io.realm.RealmResults;

public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyPostsAdapter adapter;
    private RealmHelper realmHelper;
    private String currentStudentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        // Récupérer l'ID de l'utilisateur connecté
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        currentStudentId = prefs.getString("studentId", "");

        // Initialiser RealmHelper
        realmHelper = RealmHelper.getInstance();

        // Configurer Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mes Annonces");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurer RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Charger les annonces de l'utilisateur
        loadMyPosts();

        // Configurer les actions (edit/delete)
        adapter.setOnItemClickListener(new MyPostsAdapter.OnItemClickListener() {
            @Override
            public void onEdit(LostItem item) {
                Toast.makeText(MyPostsActivity.this, "Modifier: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Lancer PostItemActivity en mode édition (travail de Ines)
            }

            @Override
            public void onDelete(LostItem item) {
                // Supprimer l'annonce
                realmHelper.deleteItem(item.getId());
                loadMyPosts();
                Toast.makeText(MyPostsActivity.this, "Annonce supprimée", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyPosts() {
        RealmResults<LostItem> myItems = realmHelper.getItemsByOwner(currentStudentId);
        adapter = new MyPostsAdapter(this, myItems);
        recyclerView.setAdapter(adapter);
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
        loadMyPosts(); // Rafraîchir quand on revient sur l'écran
    }
}