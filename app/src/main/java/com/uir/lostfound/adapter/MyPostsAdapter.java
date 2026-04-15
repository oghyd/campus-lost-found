package com.uir.lostfound.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uir.lostfound.R;
import com.uir.lostfound.model.LostItem;
import java.util.ArrayList;
import java.util.List;
import io.realm.RealmResults;
import com.uir.lostfound.utils.StatusUtils;

/**
 * MyPostsAdapter — RecyclerView adapter for the "My Posts" screen.
 *
 * Binds the current user's {@link com.uir.lostfound.model.LostItem} objects
 * to {@code item_my_post_card.xml} cards.
 *
 * Each card exposes two action buttons:
 * - Edit (pencil icon) — delegates to {@link OnItemClickListener#onEdit(com.uir.lostfound.model.LostItem)}.
 * - Delete (bin icon) — delegates to {@link OnItemClickListener#onDelete(com.uir.lostfound.model.LostItem)}.
 *
 * Status chip colours are resolved through {@link com.uir.lostfound.utils.StatusUtils}.
 *
 * Ownership: Idriss.
 */
public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder> {

    private Context context;
    private List<LostItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(LostItem item);
        void onDelete(LostItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MyPostsAdapter(Context context, RealmResults<LostItem> items) {
        this.context = context;
        this.items = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_post_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LostItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        // Type (Lost/Found)
        if ("LOST".equals(item.getType())) {
            holder.typeChip.setText("📢 PERDU");
        } else {
            holder.typeChip.setText("🔍 TROUVÉ");
        }

        // Status
        // Status (using Omar's centralized helper)
        String status = item.getStatus();

        holder.statusChip.setText(StatusUtils.getDisplayLabel(status));
        holder.statusChip.setTextColor(StatusUtils.getChipTextColor(context, status));

        // If statusChip already has a drawable background, tint it
        if (holder.statusChip.getBackground() != null) {
            holder.statusChip.getBackground().setTint(
                    StatusUtils.getChipBackgroundColor(context, status)
            );
        }

        // Boutons
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(RealmResults<LostItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, typeChip, statusChip;
        ImageButton editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            typeChip = itemView.findViewById(R.id.typeChip);
            statusChip = itemView.findViewById(R.id.statusChip);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}