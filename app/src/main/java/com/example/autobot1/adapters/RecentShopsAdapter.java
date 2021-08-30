package com.example.autobot1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobot1.R;
import com.example.autobot1.adapters.RecentShopsAdapter.RecentShopsViewHolder;
import com.example.autobot1.models.ShopItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentShopsAdapter extends RecyclerView.Adapter<RecentShopsViewHolder> {
    private final Context context;
    private final List<ShopItem> shopItems;
    private OnItemClick listener;

    public RecentShopsAdapter(Context context, List<ShopItem> shopItems) {
        this.context = context;
        this.shopItems = shopItems;
    }

    public interface OnItemClick {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentShopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentShopsViewHolder(
                LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentShopsViewHolder holder, int position) {
        holder.bind(shopItems.get(position));
    }

    @Override
    public int getItemCount() {
        return shopItems.size();
    }

    public static class RecentShopsViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView location;
        private final TextView description;
        private final ImageView shopImage;

        public RecentShopsViewHolder(@NonNull View itemView, OnItemClick listener) {
            super(itemView);
            title = itemView.findViewById(R.id.shop_title_text_view);
            location = itemView.findViewById(R.id.shop_location_text_view);
            description = itemView.findViewById(R.id.shop_description_text_view);
            shopImage = itemView.findViewById(R.id.shop_item_image_view);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }

        public void bind(ShopItem shopItem) {
            title.setText(shopItem.getTitle());
            location.setText(String.format("Lat: %s\nLong: %s", shopItem.getLocation().getLatitude(), shopItem.getLocation().getLatitude()));
            description.setText(shopItem.getDescription());
            Picasso.get().load(shopItem.getImageUrl()).placeholder(R.drawable.account_box_type).into(shopImage);
        }
    }
}
