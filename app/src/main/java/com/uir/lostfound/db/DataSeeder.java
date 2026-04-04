package com.uir.lostfound.db;

import com.uir.lostfound.model.LostItem;
import java.util.UUID;

public class DataSeeder {
    public static void seedData(RealmHelper realmHelper) {
        // Ajouter quelques items de test
        if (realmHelper.getAllItems().size() == 0) {
            LostItem item1 = new LostItem();
            item1.setId(UUID.randomUUID().toString());
            item1.setTitle("MacBook Pro 16 pouces");
            item1.setDescription("Ordinateur portable Apple, couleur gris, dans une housse noire");
            item1.setLocation("Bibliothèque, 2ème étage");
            item1.setCategory("Electronics");
            item1.setType("LOST");
            item1.setStatus("OPEN");
            item1.setOwnerStudentId("UIR12345");
            item1.setOwnerName("Idriss");
            realmHelper.insertItem(item1);

            LostItem item2 = new LostItem();
            item2.setId(UUID.randomUUID().toString());
            item2.setTitle("Carte d'étudiant");
            item2.setDescription("Carte UIR au nom de Mohamed Amine");
            item2.setLocation("Amphi A");
            item2.setCategory("Documents");
            item2.setType("FOUND");
            item2.setStatus("OPEN");
            item2.setOwnerStudentId("UIR12346");
            item2.setOwnerName("Mohamed");
            realmHelper.insertItem(item2);
        }
    }
}