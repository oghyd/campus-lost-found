package com.uir.lostfound.fragment;

import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.uir.lostfound.R;
import com.uir.lostfound.db.RealmHelper;
import com.uir.lostfound.model.LostItem;
import com.uir.lostfound.utils.StatusUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemDetailFragment extends Fragment {

    private static final String ARG_ITEM_ID = "item_id";

    // Factory method recommandé pour les Fragments
    public static ItemDetailFragment newInstance(String itemId) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String itemId = getArguments() != null ? getArguments().getString(ARG_ITEM_ID) : null;
        if (itemId == null) return;

        LostItem item = RealmHelper.getInstance().getItemById(itemId);
        if (item == null) return;

        bindItem(view, item);
    }

    // Remplit tous les champs de l'UI avec les données Realm
    private void bindItem(View view, LostItem item) {
        TextView tvTitle       = view.findViewById(R.id.tv_title);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        TextView tvLocation    = view.findViewById(R.id.tv_location);
        TextView tvCategory    = view.findViewById(R.id.tv_category);
        TextView tvOwner       = view.findViewById(R.id.tv_owner);
        TextView tvDate        = view.findViewById(R.id.tv_date);
        TextView chipType      = view.findViewById(R.id.chip_type);
        TextView chipStatus    = view.findViewById(R.id.chip_status);
        ImageView imgPhoto     = view.findViewById(R.id.img_photo);

        tvTitle.setText(item.getTitle());
        tvDescription.setText(item.getDescription());
        tvLocation.setText(item.getLocation());
        tvCategory.setText(item.getCategory());
        tvOwner.setText(item.getOwnerName() + " (" + item.getOwnerStudentId() + ")");

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(item.getTimestamp())));

        // Chip Type : LOST = rouge, FOUND = vert
        setChipStyle(chipType, item.getType(),
                "LOST".equals(item.getType()) ? 0xFFE53935 : 0xFF43A047);

        // Chip Status : couleur via StatusUtils d'Omar
        setChipStyle(chipStatus, item.getStatus(),
                StatusUtils.getChipColor(item.getStatus()));

        // Photo : affiche seulement si le fichier existe
        if (item.getPhotoPath() != null && !item.getPhotoPath().isEmpty()) {
            File imgFile = new File(item.getPhotoPath());
            if (imgFile.exists()) {
                imgPhoto.setImageBitmap(BitmapFactory.decodeFile(item.getPhotoPath()));
                imgPhoto.setVisibility(View.VISIBLE);
            }
        }
    }

    // Helper : background arrondi coloré pour les chips
    private void setChipStyle(TextView chip, String text, int color) {
        chip.setText(text);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(24f);
        bg.setColor(color);
        chip.setBackground(bg);
    }
}