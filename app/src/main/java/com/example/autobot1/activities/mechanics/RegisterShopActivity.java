package com.example.autobot1.activities.mechanics;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.autobot1.activities.landing.MapActivity;
import com.example.autobot1.databinding.ActivityRegisterShopBinding;
import com.example.autobot1.models.ShopItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RegisterShopActivity extends AppCompatActivity {
    private static final String TAG = "RegisterShopActivity";
    private ActivityRegisterShopBinding binding;
    private Location lastLocation;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Autobot");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
        Snackbar.make(binding.getRoot(), "Make sure you are the shop during this process so we can determine shop's actual position", Snackbar.LENGTH_LONG).show();
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 700);
        } else {
            @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
            task.addOnCompleteListener(task1 -> {
                lastLocation = task1.getResult();
                binding.shopLocationEt.setText(String.format("%s,%s", lastLocation.getLatitude(), lastLocation.getLongitude()));
                getAddress();
            });
        }
        binding.shopIv.setOnClickListener(view -> {
            if (canReadStorage()) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityIfNeeded(intent, 300);
            } else {
                ActivityCompat.requestPermissions(RegisterShopActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 400);
            }
        });
        binding.submitBtn.setOnClickListener(view -> {
            progressDialog.show();
            String name = binding.shopNameEt.getText().toString().trim();
            String description = binding.shopDescriptionEt.getText().toString().trim();
            String location = binding.shopLocationEt.getText().toString().trim();
            String address = binding.shopAddressEt.getText().toString().trim();
            String contact = binding.shopContactEt.getText().toString().trim();
            if (name.isEmpty() || description.isEmpty() || location.isEmpty() || address.isEmpty() || contact.isEmpty()) {
                Toast.makeText(RegisterShopActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (imageUri.getPath().isEmpty()) {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    StorageReference reference = FirebaseStorage.getInstance().getReference("shop-images/" + FirebaseAuth.getInstance().getUid());
                    reference.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                Log.i(TAG, "onCreate: -> image uploaded successfully");
                                LatLng loc = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                                ShopItem shopItem = new ShopItem(name, loc.latitude, loc.longitude, description, uri.toString(), contact,binding.shopAddressEt.getText().toString());
                                uploadShop(shopItem);
                            }));
                }
            }
        });
    }

    private void getAddress() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            if (addresses == null || addresses.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Try again", Snackbar.LENGTH_LONG).show();
            } else {
                Address address = addresses.get(0);
                List<String> ad = new ArrayList<>();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    ad.add(address.getAddressLine(i));
                }
                binding.shopAddressEt.setText(ad.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadShop(ShopItem shopItem) {
        FirebaseDatabase.getInstance().getReference("shops/" + FirebaseAuth.getInstance().getUid())
                .setValue(shopItem).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.isComplete()) {
                progressDialog.dismiss();
                startActivity(new Intent(RegisterShopActivity.this, MapActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==400 && Arrays.equals(permissions,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}) && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityIfNeeded(intent,300);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.shopIv.setImageBitmap(bm);
            } catch (Exception e) {
                Log.i(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }

    private boolean canReadStorage() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
