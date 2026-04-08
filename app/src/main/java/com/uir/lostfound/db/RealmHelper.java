package com.uir.lostfound.db;

import com.uir.lostfound.model.LostItem;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

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

    public void insertItem(LostItem item) {
        realm.executeTransaction(r -> {
            if (item.getId() == null || item.getId().isEmpty()) {
                item.setId(UUID.randomUUID().toString());
            }
            item.setTimestamp(System.currentTimeMillis());
            r.insertOrUpdate(item);
        });
    }

    public RealmResults<LostItem> getAllItems() {
        return realm.where(LostItem.class)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();
    }

    public RealmResults<LostItem> getItemsByOwner(String studentId) {
        return realm.where(LostItem.class)
                .equalTo("ownerStudentId", studentId)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();
    }

    public LostItem getItemById(String id) {
        return realm.where(LostItem.class)
                .equalTo("id", id)
                .findFirst();
    }

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

    public void close() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        instance = null;
    }
}