package com.uir.lostfound.db;

import com.uir.lostfound.model.LostItem;
import java.util.UUID;

/**
 * DataSeeder — inserts demo data into Realm on first launch.
 *
 * Called from {@link com.uir.lostfound.MyApplication#onCreate()} guarded by a size check:
 * if Realm already contains items the seeder does nothing (idempotent).
 *
 * Seed contents:
 * - 3 registered users: STU001 (Hasnae), STU002 (Fatima), STU003 (Youssef).
 * - 10 mixed LOST/FOUND items across all categories and statuses, staggered by 1 hour each.
 */
public class DataSeeder {

    public static void seedData(RealmHelper realmHelper) {
        if (realmHelper.getAllItems().size() > 0) {
            return;
        }

        // ── 3 Students ──
        String[][] users = {
                {"STU001", "Hasnae Ghiyati"},
                {"STU002", "Fatima Zahra"},
                {"STU003", "Youssef Benali"}
        };

        // ── 10 Lost/Found Items ──
        Object[][] items = {
                {"MacBook Pro 14\"",       "Silver laptop left in lecture hall",         "Amphi A",                "Electronics", "LOST",  "OPEN",     0},
                {"Student ID Card",        "UIR card found near cafeteria entrance",    "Cafeteria",              "Documents",   "FOUND", "OPEN",     1},
                {"Black Backpack",         "JanSport bag with notebooks inside",        "Library, 2nd floor",     "Bags",        "LOST",  "OPEN",     2},
                {"Car Keys (Toyota)",      "Set of 3 keys with red keychain",           "Parking B",              "Keys",        "LOST",  "CLAIMED",  0},
                {"Blue Hoodie",            "Nike hoodie size M, left after gym class",  "Sports Complex",         "Clothing",    "LOST",  "OPEN",     1},
                {"AirPods Pro Case",       "White case found on bench outside block C", "Block C entrance",       "Electronics", "FOUND", "OPEN",     2},
                {"Calculus Textbook",      "Stewart Calculus 8th edition",              "Room 204, Block A",      "Documents",   "LOST",  "RETURNED", 0},
                {"USB Flash Drive 64GB",   "SanDisk silver, has sticker on it",         "Computer Lab 3",         "Electronics", "FOUND", "OPEN",     1},
                {"Prescription Glasses",   "Ray-Ban frames, brown case",               "Amphi B",                "Other",       "LOST",  "CLAIMED",  2},
                {"Wallet (Brown Leather)", "Contains bus pass, no cash",               "Student Lounge, Block D", "Other",       "LOST",  "OPEN",     0}
        };

        long now = System.currentTimeMillis();

        for (int i = 0; i < items.length; i++) {
            Object[] d = items[i];
            int userIdx = (int) d[6]; // index into users[]

            LostItem item = new LostItem();
            item.setId(UUID.randomUUID().toString());
            item.setTitle((String) d[0]);
            item.setDescription((String) d[1]);
            item.setLocation((String) d[2]);
            item.setCategory((String) d[3]);
            item.setType((String) d[4]);
            item.setStatus((String) d[5]);
            item.setTimestamp(now - (long)(i + 1) * 3600_000); // stagger by 1h each
            item.setOwnerStudentId(users[userIdx][0]);
            item.setOwnerName(users[userIdx][1]);
            realmHelper.insertItem(item);
        }
    }
}
