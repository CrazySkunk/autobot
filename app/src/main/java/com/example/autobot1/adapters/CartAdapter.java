package com.example.autobot1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autobot1.R;
import com.example.autobot1.adapters.ProductAdapter.ProductViewHolder;
import com.example.autobot1.databinding.CartItemBinding;
import com.example.autobot1.models.ProductItem;
import com.example.autobot1.models.ProductItemCart;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<ProductItemCart> productItemList;
    private OnItemClick listener;

    public CartAdapter(List<ProductItemCart> productItemList) {
        this.productItemList = productItemList;
    }

    public interface OnItemClick{
        void onItemClick(int position,View view,View v);
    }
    public void setOnItemClickListener(OnItemClick listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_item,
                                parent,
                                false),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(productItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return productItemList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        CartItemBinding binding;

        public CartViewHolder(View itemView,OnItemClick listener) {
            super(itemView);
            binding = CartItemBinding.bind(itemView);
            binding.increment.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position,itemView,view);
                }
            });
            binding.decrement.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position,itemView,view);
                }
            });
            binding.deleteProduct.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position,itemView,view);
                }
            });
        }

        protected void bind(ProductItemCart item) {
           binding.titleProduct.setText(item.getTitle());
           binding.descriptionProduct.setText(item.getDescription());
           binding.priceProduct.setText(item.getPrice());
           binding.quantityTvProduct.setText(item.getQuantity());
           binding.totalProduct.setText(item.getTotal());
           Picasso.get().load(item.getImageUrl()).placeholder(R.drawable.account_box_type).into(binding.productImage);
        }
    }
}
