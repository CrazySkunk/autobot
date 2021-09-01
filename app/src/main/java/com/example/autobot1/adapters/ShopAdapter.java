package com.example.autobot1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.FavShopViewModel;
import com.example.autobot1.databinding.ShopItemBinding;
import com.example.autobot1.models.ShopItem;
import com.example.autobot1.models.ShopItemFav;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private final List<ShopItem> shopItems;
    private OnItemClick listener;
    private FavShopViewModel viewModel;

    public ShopAdapter(List<ShopItem> shopItems) {
        this.shopItems = shopItems;
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(FavShopViewModel.class);
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
        holder.binding.favIcon.setOnClickListener(view -> {
            ShopItem item = shopItems.get(position);
            viewModel.addCartItem(new ShopItemFav(0,item.getTitle(),String.valueOf(item.getLocation().getLatitude()),String.valueOf(item.getLocation().getLongitude()),item.getDescription(), item.getImageUrl(), item.getContact(), true));
        });
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
            binding.shopLocationTextView.setText(String.format("lat: %s,long: %s", shopItem.getLocation().getLatitude(), shopItem.getLocation().getLongitude()));
            binding.shopDescriptionTextView.setText(shopItem.getDescription());
            Picasso.get().load(shopItem.getImageUrl()).placeholder(R.drawable.bot).into(binding.shopItemImageView);
        }
    }
}
