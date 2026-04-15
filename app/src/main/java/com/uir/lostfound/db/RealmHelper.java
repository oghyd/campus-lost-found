package com.uir.lostfound.db;

import com.uir.lostfound.model.LostItem;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * RealmHelper — singleton CRUD façade for the Realm local database.
 *
 * Provides all read and write operations on {@link com.uir.lostfound.model.LostItem}.
 * All write operations are wrapped in {@code realm.executeTransaction()} as required by Realm.
 *
 * Usage:
 * <pre>
 *   RealmHelper helper = RealmHelper.getInstance();
 *   helper.insertItem(item);
 *   RealmResults&lt;LostItem&gt; all = helper.getAllItems();
 * </pre>
 *
 * Call {@link #close()} in {@code onDestroy()} of the owning Activity when the instance
 * is no longer needed (typically MyPostsActivity). Other Activities rely on the singleton
 * staying open for the app's lifetime.
 *
 * Ownership: Idriss.
 */
public class RealmHelper {
    private static RealmHelper instance;
    private final Realm realm;

    private RealmHelper() {
        realm = Realm.getDefaultInstance();
    }

    public static synchronized RealmHelper getInstance() {
        if (instance == null) {
            instance = new RealmHelper();
        }
        return instance;
    }

    /**
     * Inserts or updates a LostItem in Realm.
     * Auto-generates a UUID id and sets the timestamp if not already set.
     */
    public void insertItem(LostItem item) {
        realm.executeTransaction(r -> {
            if (item.getId() == null || item.getId().isEmpty()) {
                item.setId(UUID.randomUUID().toString());
            }
            item.setTimestamp(System.currentTimeMillis());
            r.insertOrUpdate(item);
        });
    }

    /** Returns all items sorted by timestamp descending (newest first). */
    public RealmResults<LostItem> getAllItems() {
        return realm.where(LostItem.class)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();
    }

    /** Returns all items owned by the given student ID, sorted by timestamp descending. */
    public RealmResults<LostItem> getItemsByOwner(String studentId) {
        return realm.where(LostItem.class)
                .equalTo("ownerStudentId", studentId)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();
    }

    /** Returns the first item matching the given UUID id, or null if not found. */
    public LostItem getItemById(String id) {
        return realm.where(LostItem.class)
                .equalTo("id", id)
                .findFirst();
    }

    /** Permanently removes the item with the given id from Realm. No-op if not found. */
    public void deleteItem(String id) {
        realm.executeTransaction(r -> {
            LostItem item = r.where(LostItem.class)
                    .equalTo("id", id)
                    .findFirst();
            if (item != null) {
                item.deleteFromRealm();
            }
        });
    }

    /**
     * Updates only the {@code status} field of the item with the given id.
     * Use {@link com.uir.lostfound.utils.StatusUtils} constants for the newStatus value.
     */
    public void updateItemStatus(String id, String newStatus) {
        realm.executeTransaction(r -> {
            LostItem item = r.where(LostItem.class)
                    .equalTo("id", id)
                    .findFirst();
            if (item != null) {
                item.setStatus(newStatus);
            }
        });
    }

    /**
     * Copies editable fields from {@code updated} onto the existing item with the given id.
     * Preserves the original id, status, and ownerStudentId.
     * photoPath is only overwritten when the updated object provides a non-null value.
     */
    public void updateItem(String id, LostItem updated) {
        realm.executeTransaction(r -> {
            LostItem existing = r.where(LostItem.class)
                    .equalTo("id", id)
                    .findFirst();
            if (existing != null) {
                existing.setTitle(updated.getTitle());
                existing.setDescription(updated.getDescription());
                existing.setLocation(updated.getLocation());
                existing.setCategory(updated.getCategory());
                existing.setType(updated.getType());
                existing.setTimestamp(updated.getTimestamp());
                if (updated.getPhotoPath() != null) {
                    existing.setPhotoPath(updated.getPhotoPath());
                }
            }
        });
    }

    /**
     * Closes the Realm instance and resets the singleton so a fresh instance
     * can be created on next {@link #getInstance()} call.
     */
    public void close() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        instance = null;
    }
}