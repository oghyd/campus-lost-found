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
        switch (item.getStatus()) {
            case "CLAIMED":
                holder.statusChip.setText("🟡 RÉCLAMÉ");
                break;
            case "RETURNED":
                holder.statusChip.setText("✅ RESTITUÉ");
                break;
            default:
                holder.statusChip.setText("🟢 OUVERT");
                break;
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