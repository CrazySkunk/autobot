package com.example.autobot1.activities.landing.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.CartViewModel;
import com.example.autobot1.databinding.FragmentDetailBinding;
import com.example.autobot1.models.ProductItem;
import com.example.autobot1.models.ProductItemCart;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailFragment extends Fragment {
    private FragmentDetailBinding binding;
    public static final String PRODUCT = "product";
    private ProductItem item;
    private CartViewModel cartViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        if (getArguments() != null) {
            item = getArguments().getParcelable(PRODUCT);
        }
    }

    public static DetailFragment newInstance(ProductItem productItem) {
        Bundle args = new Bundle();
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        args.putParcelable(PRODUCT, productItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        binding.titleDetail.setText(item.getTitle());
        binding.descriptionDetail.setText(item.getDescription());
        binding.quantityDetailTv.setText("1");
        Picasso.get().load(item.getImageUrl()).placeholder(R.drawable.account_box_type).into(binding.imageDetail);
        binding.totalDetailTv.setText(Integer.parseInt(Objects.requireNonNull(binding.quantityDetailTv.getText()).toString()) * Integer.parseInt(item.getPrice()));
        binding.increaseCount.setOnClickListener(view -> increaseQuantity(Objects.requireNonNull(binding.quantityDetailTv.getText()).toString()));
        binding.decreaseCount.setOnClickListener(view -> decreaseQuantity(view, Objects.requireNonNull(binding.quantityDetailTv.getText()).toString()));
        binding.addToCartBtn.setOnClickListener(view -> addToCart(Objects.requireNonNull(binding.quantityDetailTv.getText()).toString(),item));
        binding.proceedToCart.setOnClickListener(view -> goToCart());
        return binding.getRoot();
    }

    private void goToCart() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,new CartFragment())
                .commit();
    }

    private void addToCart(String quantity, ProductItem item) {
        ProductItemCart productItemCart = new ProductItemCart(0,item.getTitle(),item.getDescription(),item.getPrice(), item.getImageUrl(), quantity,String.valueOf(Integer.parseInt(quantity)*Integer.parseInt(item.getPrice())));
        cartViewModel.addCartItem(productItemCart);
        Toast.makeText(requireContext(), "Successfully added item to cart", Toast.LENGTH_SHORT).show();
    }

    private void decreaseQuantity(View view, String quantity) {
        int q = Integer.parseInt(quantity);
        if (q == 1) {
            Snackbar.make(view, "Cannot go below 1 item", Snackbar.LENGTH_LONG).show();
            binding.quantityDetailTv.setText(q);
            binding.totalDetailTv.setText(q * Integer.parseInt(item.getPrice()));
        } else {
            q -= 1;
            binding.quantityDetailTv.setText(q);
            int total = q * Integer.parseInt(item.getPrice());
            binding.totalDetailTv.setText(total);
        }
    }

    private void increaseQuantity(String quantity) {
        int q = Integer.parseInt(quantity);
        q += 1;
        int total = q * Integer.parseInt(item.getPrice());
        binding.quantityDetailTv.setText(q);
        binding.totalDetailTv.setText(total);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
