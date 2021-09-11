package com.example.autobot1.activities.landing.frags;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobot1.R;
import com.example.autobot1.activities.mechanics.MechanicsActivity;
import com.example.autobot1.activities.mechanics.viewmodels.BookingsViewModel;
import com.example.autobot1.adapters.BookingsAdapter;
import com.example.autobot1.databinding.FragmentBookingBinding;
import com.example.autobot1.models.Request;
import com.google.firebase.auth.FirebaseAuth;

public class BookingFragment extends Fragment {
    private FragmentBookingBinding binding;
    private BookingsViewModel bookingsViewModel;
    private BookingsAdapter adapter;
    SendMessage SM;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingsViewModel = new ViewModelProvider(this).get(BookingsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingBinding.inflate(inflater, container, false);
        inflateList();
        return binding.getRoot();
    }

    private void inflateList() {
        ListView listView = binding.lvSchedules;
        int resource = R.layout.booking_item;
        bookingsViewModel.getMechanicBookings(FirebaseAuth.getInstance().getUid()).observe(getViewLifecycleOwner(), bookings -> {
            if (bookings.isEmpty()) {
                binding.noItemsInCartIv.setVisibility(View.VISIBLE);
                binding.noItemInCartTv.setVisibility(View.VISIBLE);
                binding.lvSchedules.setVisibility(View.GONE);
            } else {
                binding.noItemsInCartIv.setVisibility(View.GONE);
                binding.noItemInCartTv.setVisibility(View.GONE);
                binding.lvSchedules.setVisibility(View.VISIBLE);
                adapter = new BookingsAdapter(requireContext(), resource, bookings);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                adapter.setOnItemClickListener(position -> ((MechanicsActivity) requireActivity()).setMapSelected());
            }
        });
    }

    public interface SendMessage {
        void sendData(Request message);
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            SM = (SendMessage) getActivity();
//        } catch (ClassCastException e) {
//            throw new ClassCastException("Error in retrieving data. Please try again");
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}