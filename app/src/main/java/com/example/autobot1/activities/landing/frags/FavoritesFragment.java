package com.example.autobot1.activities.landing.frags;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.FavShopViewModel;
import com.example.autobot1.adapters.ShopAdapterFav;
import com.example.autobot1.databinding.FragmentFavoritesBinding;
import com.example.autobot1.models.ShopItemFav;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private FavShopViewModel viewModel;
    private List<ShopItemFav> searchFav;

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
        requireActivity().getMenuInflater().inflate(R.menu.fav_search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_fav_mechanic);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search item...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                List<ShopItemFav> search = new ArrayList<>();
                searchFav.forEach(shopItemFav -> {
                    if (shopItemFav.getTitle().contains(newText) || shopItemFav.getDescription().contains(newText) || shopItemFav.getContact().contains(newText)
                            || String.valueOf(shopItemFav.getLatitude()).contains(newText) || String.valueOf(shopItemFav.getLongitude()).contains(newText)) {
                        search.add(shopItemFav);
                    }
                });
                if (search.isEmpty()) {
                    binding.noItemInCartTvFav.setVisibility(View.VISIBLE);
                    binding.noItemInCartTvFav.setVisibility(View.VISIBLE);
                    binding.favoriteShops.setVisibility(View.GONE);
                } else {
                    binding.noItemInCartTvFav.setVisibility(View.GONE);
                    binding.noItemInCartTvFav.setVisibility(View.GONE);
                    binding.favoriteShops.setVisibility(View.VISIBLE);
                    ShopAdapterFav adapter = new ShopAdapterFav(search);
                    binding.favoriteShops.setHasFixedSize(true);
                    binding.favoriteShops.setClipToPadding(false);
                    binding.favoriteShops.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
                    binding.favoriteShops.setAdapter(adapter);
                    adapter.setOnItemClickListener(position -> requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, SpecificShopFragment.newInstance(search.get(position).getTitle()))
                            .commit());
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all_products) {
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
                searchFav = shopItemFavs;
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
