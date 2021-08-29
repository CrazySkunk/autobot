package com.example.autobot1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobot1.R;
import com.example.autobot1.databinding.ShopItemBinding;
import com.example.autobot1.models.ShopItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private final List<ShopItem> shopItems;
    private OnItemClick listener;

    public ShopAdapter(List<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }

    public interface OnItemClick{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClick listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item, parent, false),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.ShopViewHolder holder, int position) {
        holder.bind(shopItems.get(position));
    }

    @Override
    public int getItemCount() {
        return shopItems.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        private final ShopItemBinding binding;
        public ShopViewHolder(View itemView,OnItemClick listener) {
            super(itemView);
             binding = ShopItemBinding.bind(itemView);
             itemView.setOnClickListener(view -> {
                 int position = getAdapterPosition();
                 if (position!=RecyclerView.NO_POSITION){
                     listener.onItemClick(position);
                 }
             });
        }
        public void bind(ShopItem shopItem){
            binding.shopTitleTextView.setText(shopItem.getTitle());
            binding.shopLocationTextView.setText(shopItem.getLocation().getLatitude()+","+shopItem.getLocation().getLongitude());
            binding.shopDescriptionTextView.setText(shopItem.getDescription());
            Picasso.get().load(shopItem.getImageUrl()).placeholder(R.drawable.bot).into(binding.shopItemImageView);
        }
    }
}
