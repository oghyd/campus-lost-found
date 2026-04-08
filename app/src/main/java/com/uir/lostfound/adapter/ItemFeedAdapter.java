package com.uir.lostfound.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uir.lostfound.R;
import com.uir.lostfound.model.LostItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import io.realm.RealmResults;
import com.uir.lostfound.utils.StatusUtils;
public class ItemFeedAdapter extends RecyclerView.Adapter<ItemFeedAdapter.ViewHolder> {

    private Context context;
    private List<LostItem> items;
    private List<LostItem> itemsFull;

    public ItemFeedAdapter(Context context, RealmResults<LostItem> items) {
        this.context = context;
        this.items = new ArrayList<>(items);
        this.itemsFull = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LostItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.category.setText(item.getCategory());

        // Formater la date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = sdf.format(new Date(item.getTimestamp()));
        holder.timestamp.setText(date);

        // Type (Lost/Found)
        if ("LOST".equals(item.getType())) {
            holder.typeChip.setText("📢 PERDU");
            // Utilisation de ressources de couleur si possible, sinon hex
            holder.typeChip.setBackgroundColor(0xFFFFEB3B);
        } else {
            holder.typeChip.setText("🔍 TROUVÉ");
            holder.typeChip.setBackgroundColor(0xFF4CAF50);
        }

        // Status
        // Status (Omar's centralized status system)
        String status = item.getStatus();

        // Set chip text using StatusUtils
        holder.statusChip.setText(StatusUtils.getDisplayLabel(status));

        // Set chip text color
        holder.statusChip.setTextColor(StatusUtils.getChipTextColor(context, status));

        // Preferred: tint existing rounded chip background
        holder.statusChip.getBackground().setTint(
                StatusUtils.getChipBackgroundColor(context, status)
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void filter(String text) {
        items.clear();
        if (text == null || text.isEmpty()) {
            items.addAll(itemsFull);
        } else {
            String lowerCase = text.toLowerCase();
            for (LostItem item : itemsFull) {
                if ((item.getTitle() != null && item.getTitle().toLowerCase().contains(lowerCase)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCase))) {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByType(String type) {
        items.clear();
        if (type == null) {
            items.addAll(itemsFull);
        } else {
            for (LostItem item : itemsFull) {
                if (type.equals(item.getType())) {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sortByDate(boolean newestFirst) {
        java.util.Collections.sort(items, (a, b) ->
                newestFirst ? Long.compare(b.getTimestamp(), a.getTimestamp())
                            : Long.compare(a.getTimestamp(), b.getTimestamp()));
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, timestamp, typeChip, statusChip;
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            category = itemView.findViewById(R.id.category);
            timestamp = itemView.findViewById(R.id.timestamp);
            typeChip = itemView.findViewById(R.id.typeChip);
            statusChip = itemView.findViewById(R.id.statusChip);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
