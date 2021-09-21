package com.example.autobot1.activities.landing.frags;

import android.content.Intent;
import android.net.Uri;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.MechanicShopsViewModel;
import com.example.autobot1.activities.landing.viewmodels.RecentShopsViewModel;
import com.example.autobot1.adapters.ShopAdapter;
import com.example.autobot1.databinding.FragmentMechanicShopsBinding;
import com.example.autobot1.models.RecentShopItem;
import com.example.autobot1.models.ShopItem;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class MechanicShopsFragment extends Fragment {
    private RecyclerView shopsRecycler;
    private FragmentMechanicShopsBinding binding;
    private MechanicShopsViewModel viewModel;
    private RecentShopsViewModel recentShopsViewModel;
    private List<ShopItem> shops;

    public MechanicShopsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shops = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(MechanicShopsViewModel.class);
        recentShopsViewModel = new ViewModelProvider(this).get(RecentShopsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMechanicShopsBinding.inflate(inflater, container, false);
        viewModel.getShops().observe(getViewLifecycleOwner(), shopItems -> {
//            if (shopItems.isEmpty()) {
//                binding.emptyTrayIv.setVisibility(View.VISIBLE);
//                binding.emptyTrayTv.setVisibility(View.VISIBLE);
////                binding.shopsRecycler.setVisibility(View.INVISIBLE);
//            } else {
            binding.emptyTrayIv.setVisibility(View.GONE);
            binding.emptyTrayTv.setVisibility(View.GONE);
            binding.shopsRecycler.setVisibility(View.VISIBLE);
            shopsRecycler = binding.shopsRecycler;
            ShopAdapter shopAdapter = new ShopAdapter(shopItems, requireActivity().getApplication());
            shops = shopItems;
            shopsRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
            shopsRecycler.setClipToPadding(false);
            shopsRecycler.hasFixedSize();
            shopsRecycler.setAdapter(shopAdapter);
            shopAdapter.setOnItemLongClickListener(position -> {

                go(shopItems.get(position).getGeocode());
            });
            shopAdapter.setOnItemClickListener(position -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_container,
                                SpecificShopFragment.newInstance(shopItems.get(position).getTitle()));
                recentShopsViewModel.addRecentShop(new RecentShopItem(
                        0,
                        shopItems.get(position).getTitle(),
                        shopItems.get(position).getDescription(),
                        shopItems.get(position).getImageUrl(),
                        shopItems.get(position).getContact(),
                        String.valueOf(shopItems.get(position).getLatitude()),
                        String.valueOf(shopItems.get(position).getLongitude()),
                        false));
            });
//            }
        });

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    public void go(String geocode) {
        //d - driving w - walking b - bicycle l - two less vehicles
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + geocode + "&mode=l"));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(binding.getRoot(), "You do not have google maps", Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        requireActivity().getMenuInflater().inflate(R.menu.mechanic_search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_mechanic);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search shop....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                List<ShopItem> shopItemList = new ArrayList<>();
                shops.forEach(shopItem -> {
                    if (shopItem.getTitle().contains(newText) || shopItem.getDescription().contains(newText) || shopItem.getContact().contains(newText)
                            || String.valueOf(shopItem.getLatitude()).contains(newText) || String.valueOf(shopItem.getLongitude()).contains(newText)) {
                        shopItemList.add(shopItem);
                    }
                });
                ShopAdapter shopAdapter = new ShopAdapter(shopItemList, requireActivity().getApplication());
                shopsRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
                shopsRecycler.setClipToPadding(false);
                shopsRecycler.hasFixedSize();
                shopsRecycler.setAdapter(shopAdapter);
                shopAdapter.setOnItemClickListener(position -> {
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment_container,
                                    SpecificShopFragment.newInstance(shopItemList.get(position).getTitle()));
                    recentShopsViewModel.addRecentShop(new RecentShopItem(
                            0,
                            shopItemList.get(position).getTitle(),
                            shopItemList.get(position).getDescription(),
                            shopItemList.get(position).getImageUrl(),
                            shopItemList.get(position).getContact(),
                            String.valueOf(shopItemList.get(position).getLatitude()),
                            String.valueOf(shopItemList.get(position).getLongitude()),
                            false));
                });
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}