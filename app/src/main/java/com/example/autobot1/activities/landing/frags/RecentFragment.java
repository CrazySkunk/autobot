package com.example.autobot1.activities.landing.frags;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.adapters.RecentShopsAdapter;
import com.example.autobot1.databinding.FragmentRecentBinding;
import com.example.autobot1.models.ShopItem;

import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends Fragment {
    private FragmentRecentBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentRecentBinding.inflate(inflater,container,false);
        inflateRecycler();
        return binding.getRoot();
    }

    private void inflateRecycler() {
        RecentShopsAdapter recentShopsAdapter = new RecentShopsAdapter(requireContext(),getRecent());
        binding.recentShops.setHasFixedSize(true);
        binding.recentShops.setLayoutManager(new StaggeredGridLayoutManager(2,VERTICAL));
        binding.recentShops.setClipToPadding(false);
        binding.recentShops.setAdapter(recentShopsAdapter);
        recentShopsAdapter.setOnItemClickListener(position ->
                requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,
                        SpecificShopFragment.newInstance("name"))
                .commit());
    }

    private List<ShopItem> getRecent() {
        //todo: getRecent
        return new ArrayList<>();
    }
}
