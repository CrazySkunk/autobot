package com.example.autobot1.activities.landing.frags;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.MapActivity;
import com.example.autobot1.activities.mechanics.viewmodels.BookingsViewModel;
import com.example.autobot1.adapters.BookingsAdapter;
import com.example.autobot1.databinding.FragmentBookingBinding;
import com.example.autobot1.models.Request;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

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
                adapter.setOnItemClickListener(position -> {
                    Geocoder geocoder = new Geocoder(requireContext());
                    List<Address> addresses = geocoder.getFromLocation(bookings.get(position).getLatitude(),bookings.get(position).getLongitude(),0);
                    go(addresses.get(0).toString());
                });
            }
        });
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

    public interface SendMessage {
        void sendData(Request message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}