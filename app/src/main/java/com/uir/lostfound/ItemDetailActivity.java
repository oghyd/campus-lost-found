package com.uir.lostfound;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.fragment.ItemDetailFragment;
import com.uir.lostfound.model.LostItem;
import com.uir.lostfound.utils.NotificationHelper;
import com.uir.lostfound.utils.SessionManager;

/**
 * ItemDetailActivity — shows the full details of a single lost/found item.
 *
 * Delegates the display to {@link com.uir.lostfound.fragment.ItemDetailFragment}.
 * Also owns the status-transition buttons:
 * - "Mark as Claimed" — visible to non-owners when status = OPEN.
 * - "Confirm Returned" — visible to the item owner when status = CLAIMED.
 *
 * Receives the item id via Intent extra {@code "item_id"}.
 *
 * Ownership: Mona (fragment + dialogs); Omar (status flow, session check).
 */
public class ItemDetailActivity extends AppCompatActivity {

    private String itemId;
    private LostItem item;
    private SessionManager sessionManager;
    private RealmHelper realmHelper;
    private Button btnClaim;
    private Button btnConfirmReturned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Item Detail");
        }

        sessionManager = new SessionManager(this);
        realmHelper = RealmHelper.getInstance();

        // Récupère l'id depuis l'Intent (envoyé par ItemFeedActivity)
        itemId = getIntent().getStringExtra("item_id");
        if (itemId == null) {
            Toast.makeText(this, "Erreur : item introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        item = realmHelper.getItemById(itemId);
        if (item == null) {
            Toast.makeText(this, "Erreur : item introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnClaim = findViewById(R.id.btn_claim);
        btnConfirmReturned = findViewById(R.id.btn_confirm_returned);

        loadFragment();
        setupButtons();
    }

    /** Instantiates ItemDetailFragment with the item id and commits it into fragment_container. */
    private void loadFragment() {
        ItemDetailFragment fragment = ItemDetailFragment.newInstance(itemId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Shows the correct action button(s) based on the current user's role and item status:
     * - Non-owner + status OPEN  → "Mark as Claimed" button visible.
     * - Owner       + status CLAIMED → "Confirm Returned" button visible.
     */
    private void setupButtons() {
        // ⚠️ Adapte getStudentId() selon ce qu'Omar a implémenté dans SessionManager
        String currentUserId = sessionManager.getStudentId();
        boolean isOwner = item.getOwnerStudentId().equals(currentUserId);
        String status = item.getStatus();

        // Non-propriétaire peut claim un item OPEN
        if (!isOwner && "OPEN".equals(status)) {
            btnClaim.setVisibility(View.VISIBLE);
        }

        // Propriétaire peut confirmer RETURNED quand status = CLAIMED
        if (isOwner && "CLAIMED".equals(status)) {
            btnConfirmReturned.setVisibility(View.VISIBLE);
        }

        btnClaim.setOnClickListener(v -> {
            btnClaim.setEnabled(false);
            showClaimDialog();
        });
        btnConfirmReturned.setOnClickListener(v -> {
            btnConfirmReturned.setEnabled(false);
            confirmReturned();
        });
    }

    /**
     * Displays a confirmation dialog for claiming an item.
     * On confirmation: updates status to CLAIMED and sends a notification to the owner.
     */
    private void showClaimDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Marquer comme Claimed ?")
                .setMessage("Êtes-vous sûr ? Le propriétaire sera notifié.")
                .setPositiveButton("Oui, Claim", (dialog, which) -> {
                    realmHelper.updateItemStatus(itemId, "CLAIMED");
                    NotificationHelper.sendClaimedNotification(
                            this, item.getOwnerName(), item.getTitle());
                    Toast.makeText(this, "Item marqué comme claimed !", Toast.LENGTH_SHORT).show();
                    btnClaim.setVisibility(View.GONE);
                    recreate(); // Recharge l'Activity pour rafraîchir l'UI
                })
                .setNegativeButton("Annuler", (dialog, which) -> btnClaim.setEnabled(true))
                .setOnCancelListener(dialog -> btnClaim.setEnabled(true))
                .show();
    }

    /** Displays a confirmation dialog for marking the item as returned (CLAIMED → RETURNED). */
    private void confirmReturned() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer le retour")
                .setMessage("Confirmer que l'objet a été rendu ?")
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    realmHelper.updateItemStatus(itemId, "RETURNED");
                    Toast.makeText(this, "Item marqué comme retourné !", Toast.LENGTH_SHORT).show();
                    btnConfirmReturned.setVisibility(View.GONE);
                    recreate();
                })
                .setNegativeButton("Annuler", (dialog, which) -> btnConfirmReturned.setEnabled(true))
                .setOnCancelListener(dialog -> btnConfirmReturned.setEnabled(true))
                .show();
    }

    // Menu overflow : Delete — visible seulement au propriétaire
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (item != null) {
            String currentUserId = sessionManager.getStudentId();
            boolean isOwner = item.getOwnerStudentId().equals(currentUserId);
            if (isOwner) {
                menu.add(0, 1, 0, "Supprimer")
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == 1) {
            showDeleteDialog();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /** Displays a confirmation dialog before permanently deleting the item from Realm. */
    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le post")
                .setMessage("Supprimer définitivement ce post ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    realmHelper.deleteItem(itemId);
                    Toast.makeText(this, "Post supprimé", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}