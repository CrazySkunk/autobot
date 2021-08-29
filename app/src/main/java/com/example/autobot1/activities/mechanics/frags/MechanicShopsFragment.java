package com.example.autobot1.activities.mechanics.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.frags.SpecificShopFragment;
import com.example.autobot1.activities.landing.viewmodels.MechanicShopsViewModel;
import com.example.autobot1.adapters.ShopAdapter;
import com.example.autobot1.databinding.FragmentMechanicShopsBinding;
import com.example.autobot1.models.ShopItem;

import java.util.List;


public class MechanicShopsFragment extends Fragment {
    private RecyclerView shopsRecycler;
    private FragmentMechanicShopsBinding binding;
    private MechanicShopsViewModel viewModel;

    public MechanicShopsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MechanicShopsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMechanicShopsBinding.inflate(inflater, container, false);
        MutableLiveData<List<ShopItem>> shops = viewModel.getShops();
        shops.observe(getViewLifecycleOwner(), shopItems -> {
            if (shopItems.isEmpty()) {
                binding.emptyTrayIv.setVisibility(View.VISIBLE);
                binding.emptyTrayTv.setVisibility(View.VISIBLE);
                binding.shopsRecycler.setVisibility(View.INVISIBLE);
            } else {
                shopsRecycler = binding.shopsRecycler;
                ShopAdapter shopAdapter = new ShopAdapter(shopItems);
                shopsRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
                shopsRecycler.setClipToPadding(false);
                shopsRecycler.hasFixedSize();
                shopsRecycler.setAdapter(shopAdapter);
                shopAdapter.setOnItemClickListener(position ->
                        requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_container,
                                SpecificShopFragment.newInstance(shopItems.get(position).getTitle())));
            }
        });

        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        requireActivity().getMenuInflater().inflate(R.menu.mechanic_shop_menu, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}