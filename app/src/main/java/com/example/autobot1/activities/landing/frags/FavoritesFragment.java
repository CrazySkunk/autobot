package com.example.autobot1.activities.landing.frags;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.FavShopViewModel;
import com.example.autobot1.adapters.ShopAdapterFav;
import com.example.autobot1.databinding.FragmentFavoritesBinding;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private FavShopViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FavShopViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
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
            viewModel.deleteAllShops();
        }
        return true;
    }

    private void inflateRecycler() {
        viewModel.getAllShops().observe(getViewLifecycleOwner(), shopItemFavs -> {
            if (shopItemFavs.isEmpty()) {
                binding.noItemInCartTvFav.setVisibility(View.VISIBLE);
                binding.noItemInCartTvFav.setVisibility(View.VISIBLE);
                binding.favoriteShops.setVisibility(View.GONE);
            } else {
                binding.noItemInCartTvFav.setVisibility(View.GONE);
                binding.noItemInCartTvFav.setVisibility(View.GONE);
                binding.favoriteShops.setVisibility(View.VISIBLE);
                ShopAdapterFav adapter = new ShopAdapterFav(shopItemFavs);
                binding.favoriteShops.setHasFixedSize(true);
                binding.favoriteShops.setClipToPadding(false);
                binding.favoriteShops.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
                binding.favoriteShops.setAdapter(adapter);
                adapter.setOnItemClickListener(position -> requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, SpecificShopFragment.newInstance(shopItemFavs.get(position).getTitle()))
                        .commit());
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
