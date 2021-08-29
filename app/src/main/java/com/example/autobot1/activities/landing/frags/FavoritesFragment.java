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
import com.example.autobot1.adapters.ShopAdapter;
import com.example.autobot1.databinding.FragmentFavoritesBinding;
import com.example.autobot1.models.ShopItem;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater,container,false);
        inflateRecycler();
        return binding.getRoot();

    }

    private void inflateRecycler() {
        ShopAdapter adapter = new ShopAdapter(getShops());
        binding.favoriteShops.setHasFixedSize(true);
        binding.favoriteShops.setClipToPadding(false);
        binding.favoriteShops.setLayoutManager(new StaggeredGridLayoutManager(2,VERTICAL));
        binding.favoriteShops.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,SpecificShopFragment.newInstance("name"))
                .commit());
    }

    private List<ShopItem> getShops() {
        //todo:getFavorites
        return new ArrayList<>();
    }
}
