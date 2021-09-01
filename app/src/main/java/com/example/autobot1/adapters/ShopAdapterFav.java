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
import com.example.autobot1.models.ShopItemFav;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShopAdapterFav extends RecyclerView.Adapter<ShopAdapterFav.ShopViewHolder> {
    private final List<ShopItemFav> shopItems;
    private OnItemClick listener;
    private FavShopViewModel viewModel;

    public ShopAdapterFav(List<ShopItemFav> shopItems) {
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
    public void onBindViewHolder(@NonNull ShopAdapterFav.ShopViewHolder holder, int position) {
        holder.bind(shopItems.get(position));
        holder.binding.favIcon.setOnClickListener(view -> {
            ShopItemFav item = shopItems.get(position);
            viewModel.deleteShop(new ShopItemFav(0,item.getTitle(),item.getLatitude(), item.getLongitude(), item.getDescription(), item.getImageUrl(), item.getContact(), true));
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
        public void bind(ShopItemFav shopItem){
            binding.shopTitleTextView.setText(shopItem.getTitle());
            binding.shopLocationTextView.setText(String.format("%s,%s", shopItem.getLatitude(), shopItem.getLongitude()));
            binding.shopDescriptionTextView.setText(shopItem.getDescription());
            Picasso.get().load(shopItem.getImageUrl()).placeholder(R.drawable.bot).into(binding.shopItemImageView);
            if (shopItem.isFav()){
                binding.favIcon.setImageResource(R.drawable.favorite);
            }
        }
    }
}
