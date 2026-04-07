package com.uir.lostfound.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LostItem extends RealmObject {
    @PrimaryKey
    private String id;
    private String title;
    private String description;
    private String location;
    private String category;
    private String type; // "LOST" ou "FOUND"
    private String status; // "OPEN", "CLAIMED", "RETURNED"
    private String photoPath;
    private long timestamp;
    private String ownerStudentId;
    private String ownerName;

    // Constructeur vide requis par Realm
    public LostItem() {}

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getOwnerStudentId() { return ownerStudentId; }
    public void setOwnerStudentId(String ownerStudentId) { this.ownerStudentId = ownerStudentId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}