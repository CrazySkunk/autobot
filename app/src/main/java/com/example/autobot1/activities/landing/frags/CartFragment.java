package com.example.autobot1.activities.landing.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.CartViewModel;
import com.example.autobot1.adapters.CartAdapter;
import com.example.autobot1.databinding.CartItemBinding;
import com.example.autobot1.databinding.FragmentCartBinding;
import com.example.autobot1.models.ProductItemCart;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        inflateRecycler();
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.cart_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.delete_all_products){
            confirmDeletion();
        }
        return true;
    }

    private void confirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Are you sure you want to delete all items?")
                .setPositiveButton("Yes",(dialog,which)->{
                    cartViewModel.deleteAllCartProducts();
                    Toast.makeText(requireContext(), "Successfully deleted products", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No",(dialog,which)->{

                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void inflateRecycler() {
        cartViewModel.getAllProducts().observe(getViewLifecycleOwner(), cartItems -> {
            CartAdapter cartAdapter = new CartAdapter(cartItems);
            binding.cartRecycler.setHasFixedSize(true);
            binding.cartRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            binding.cartRecycler.setClipToPadding(false);
            binding.cartRecycler.setAdapter(cartAdapter);
            cartAdapter.setOnItemClickListener((position, view, v) -> {
                CartItemBinding binding = CartItemBinding.bind(view);
                if (v == binding.increment) {
                    increment(cartItems.get(position));
                } else if (v == binding.decrement) {
                    decrement(cartItems.get(position));
                } else if (v == binding.deleteProduct) {
                    deleteProduct(cartItems.get(position));
                }
            });
        });

    }

    private void deleteProduct(ProductItemCart productItemCart) {
        confirmDeletionSingle(productItemCart);
    }

    private void confirmDeletionSingle(ProductItemCart productItemCart) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete "+productItemCart.getTitle()+"?")
                .setMessage("Are you sure you want to delete "+productItemCart.getTitle()+"?")
                .setPositiveButton("Yes",(dialog,which)->{
                    cartViewModel.deleteCartProduct(productItemCart);
                    Toast.makeText(requireContext(), "Successfully deleted products", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No",(dialog,which)->{

                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void decrement(ProductItemCart productItemCart) {
        if (Integer.parseInt(productItemCart.getQuantity()) == 1) {
            Toast.makeText(requireContext(), "Quantity cannot go below 1", Toast.LENGTH_SHORT).show();
        } else {
            productItemCart.setQuantity(String.valueOf(Integer.parseInt(productItemCart.getQuantity()) - 1));
        }
        productItemCart.setTotal(String.valueOf(Integer.parseInt(productItemCart.getPrice()) * Integer.parseInt(productItemCart.getQuantity())));
        cartViewModel.updateCartProduct(productItemCart);
    }

    private void increment(ProductItemCart productItemCart) {
        productItemCart.setQuantity(String.valueOf(Integer.parseInt(productItemCart.getQuantity()) + 1));
        productItemCart.setTotal(String.valueOf(Integer.parseInt(productItemCart.getPrice()) * Integer.parseInt(productItemCart.getQuantity())));
        cartViewModel.updateCartProduct(productItemCart);
    }

}