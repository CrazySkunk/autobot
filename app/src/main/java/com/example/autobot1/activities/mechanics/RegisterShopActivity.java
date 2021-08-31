package com.example.autobot1.activities.mechanics;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.autobot1.databinding.ActivityRegisterShopBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterShopActivity extends AppCompatActivity {
    private ActivityRegisterShopBinding binding;
    private Location lastLocation;
    private FusedLocationProviderClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 700);
        } else {
            client.getLastLocation().addOnSuccessListener(location -> lastLocation = location);
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address>addresses = geocoder.getFromLocation(lastLocation.getLatitude(),lastLocation.getLongitude(),1);
            addresses.get(0).getSubLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //todo:Finish up this activity
    }
}
