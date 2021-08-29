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
import com.example.autobot1.models.ProductItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private List<ProductItem> productItemList;
    private OnItemClick listener;

    public ProductAdapter(List<ProductItem> productItemList) {
        this.productItemList = productItemList;
    }

    public interface OnItemClick{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClick listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item,
                                parent,
                                false),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(productItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return productItemList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView title, description, price;
        private ImageView productImage;

        public ProductViewHolder(View itemView,OnItemClick listener) {
            super(itemView);
            title = itemView.findViewById(R.id.title_product);
            description = itemView.findViewById(R.id.description_product);
            price = itemView.findViewById(R.id.price_product);
            productImage = itemView.findViewById(R.id.product_image);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            });
        }

        protected void bind(ProductItem item) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            price.setText(item.getPrice());
            Picasso.get().load(item.getImageUrl()).into(productImage);
        }
    }
}
