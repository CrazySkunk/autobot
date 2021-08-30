package com.example.autobot1.activities.landing.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.SpecificShopViewModel;
import com.example.autobot1.adapters.ProductAdapter;
import com.example.autobot1.databinding.FragmentSpecificShopBinding;

public class SpecificShopFragment extends Fragment {
    private FragmentSpecificShopBinding binding;
    private SpecificShopViewModel viewModel;

    private static final String NAME = "name";

    private String name;

    public SpecificShopFragment() {
        // Required empty public constructor
    }


    public static SpecificShopFragment newInstance(String name) {
        SpecificShopFragment fragment = new SpecificShopFragment();
        Bundle args = new Bundle();
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SpecificShopViewModel.class);
        if (getArguments() != null) {
            name = getArguments().getString(NAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSpecificShopBinding.inflate(inflater, container, false);
        viewModel.getShopProducts(name).observe(getViewLifecycleOwner(),productItemList -> {
            ProductAdapter adapter = new ProductAdapter(requireContext(),productItemList);
            binding.shopRecycler.hasFixedSize();
            binding.shopRecycler.setClipToPadding(false);
            binding.shopRecycler.setClipToPadding(false);
            binding.shopRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.shopRecycler.setAdapter(adapter);
            adapter.setOnItemClickListener(position ->{
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout,DetailFragment.newInstance(productItemList.get(position)))
                        .commit();
            });
        });

        return binding.getRoot();
    }
}