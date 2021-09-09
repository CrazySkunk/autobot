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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.RecentShopsViewModel;
import com.example.autobot1.adapters.RecentShopsAdapter;
import com.example.autobot1.databinding.FragmentRecentBinding;

public class RecentFragment extends Fragment {
    private FragmentRecentBinding binding;
    private RecentShopsViewModel recentShopsViewModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recentShopsViewModel = new ViewModelProvider(this).get(RecentShopsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecentBinding.inflate(inflater, container, false);
        inflateRecycler();
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.cart_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all_products) {
            recentShopsViewModel.deleteAllRecentShops();
        }
        return true;
    }

    private void inflateRecycler() {
        recentShopsViewModel.getAllRecentShops().observe(getViewLifecycleOwner(), recentShopItems -> {
            if (recentShopItems.isEmpty()) {
                binding.emptyRecentIv.setVisibility(View.VISIBLE);
                binding.emptyRecentTv.setVisibility(View.VISIBLE);
                binding.recentShops.setVisibility(View.GONE);
            } else {
                binding.emptyRecentIv.setVisibility(View.GONE);
                binding.emptyRecentTv.setVisibility(View.GONE);
                binding.recentShops.setVisibility(View.VISIBLE);
                RecentShopsAdapter recentShopsAdapter = new RecentShopsAdapter(requireContext(), recentShopItems);
                binding.recentShops.setHasFixedSize(true);
                binding.recentShops.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
                binding.recentShops.setClipToPadding(false);
                binding.recentShops.setAdapter(recentShopsAdapter);
                recentShopsAdapter.setOnItemClickListener(position ->
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_layout,
                                        SpecificShopFragment.newInstance(recentShopItems.get(position).getTitle()))
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
