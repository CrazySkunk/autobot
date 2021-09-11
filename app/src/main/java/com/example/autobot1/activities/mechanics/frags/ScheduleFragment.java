package com.example.autobot1.activities.mechanics.frags;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.frags.ScheduleItemLayout;
import com.example.autobot1.activities.mechanics.models.Bookings;
import com.example.autobot1.activities.mechanics.viewmodels.BookingsViewModel;
import com.example.autobot1.adapters.BookingsAdapter;
import com.example.autobot1.databinding.FragmentScheduleBinding;
import com.example.autobot1.databinding.ScheduleItemLayoutBinding;
import com.example.autobot1.models.ScheduleItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScheduleFragment extends Fragment {
    private FragmentScheduleBinding binding;
    private BookingsViewModel viewModel;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BookingsViewModel.class);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        inflateListView();
        binding.addScheduleFab.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), ScheduleItemLayout.class));
        });
        return binding.getRoot();
    }

    private void inflateListView() {
        ListView listView = binding.lvSchedules;
        int resource = R.layout.booking_item;
        viewModel.getMechanicBookings(FirebaseAuth.getInstance().getUid()).observe(getViewLifecycleOwner(), bookings -> {
            if (bookings.isEmpty()) {
                binding.emptyTrayIv1.setVisibility(View.VISIBLE);
                binding.emptyTrayTv.setVisibility(View.VISIBLE);
                binding.lvSchedules.setVisibility(View.GONE);
            } else {
                binding.emptyTrayIv1.setVisibility(View.GONE);
                binding.emptyTrayTv.setVisibility(View.GONE);
                binding.lvSchedules.setVisibility(View.VISIBLE);
                BookingsAdapter adapter = new BookingsAdapter(requireContext(), resource, bookings);
                listView.setAdapter(adapter);
            }
        });

    }

}