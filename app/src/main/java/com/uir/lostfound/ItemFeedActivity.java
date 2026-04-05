package com.uir.lostfound;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uir.lostfound.adapter.ItemFeedAdapter;
import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.model.LostItem;
import io.realm.RealmResults;

public class ItemFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemFeedAdapter adapter;
    private RealmHelper realmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realmHelper != null) {
            realmHelper.close();
        }
    }
}