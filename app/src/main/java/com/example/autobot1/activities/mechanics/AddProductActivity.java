package com.example.autobot1.activities.mechanics;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.autobot1.databinding.ActivityAddProductBinding;
import com.example.autobot1.models.ProductItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivity";
    private ActivityAddProductBinding binding;
    private String title,description,price;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageProd.setOnClickListener(view -> {
            if (canReadStorage()){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityIfNeeded(intent,800);
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},800);
            }
        });
        binding.addBtn.setOnClickListener(view -> {
            title = binding.titleEtProd.getText().toString();
            description = binding.descriptionEtProd.getText().toString();
            price = binding.priceEt.getText().toString();
            if (title.isEmpty() || description.isEmpty() || price.isEmpty()){
                Toast.makeText(this, "Fill all fields..", Toast.LENGTH_SHORT).show();
            }else {
                if (imageUri.getPath().isEmpty()){
                    Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show();
                }else {
                    StorageReference ref = FirebaseStorage.getInstance().getReference("product_images/"+FirebaseAuth.getInstance().getUid());
                            ref.putFile(imageUri).addOnCompleteListener(task -> {
                                if (task.isComplete() && task.isSuccessful()){
                                    String url = ref.getDownloadUrl().toString();
                                    ProductItem item = new ProductItem(title,description,price,url);
                                    FirebaseDatabase.getInstance().getReference("shops/"+FirebaseAuth.getInstance().getUid())
                                            .push()
                                            .setValue(item)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful() && task1.isComplete()){
                                                    startActivity(new Intent(AddProductActivity.this,MechanicsActivity.class));
                                                    finish();
                                                }
                                            });
                                }
                            });
                }
            }
        });
    }
    private boolean canReadStorage(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && Arrays.equals(permissions, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 200);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                binding.imageProd.setImageBitmap(bm);
            } catch (Exception e) {
                Log.i(TAG, "onActivityResult: Exception " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}