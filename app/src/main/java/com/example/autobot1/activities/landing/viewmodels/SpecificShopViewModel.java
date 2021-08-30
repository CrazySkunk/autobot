package com.example.autobot1.activities.landing.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.autobot1.models.ProductItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SpecificShopViewModel extends AndroidViewModel {
    private static final String TAG = "SpecificShopViewModel";
    private final List<ProductItem> productItems;
    private final MutableLiveData<List<ProductItem>> mutableProducts;

    public SpecificShopViewModel(Application application) {
        super(application);
        productItems = new ArrayList<>();
        mutableProducts = new MutableLiveData<>();
    }

    public MutableLiveData<List<ProductItem>> getShopProducts(String name) {
        FirebaseDatabase.getInstance().getReference("shops/" + name + "/products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            ProductItem item = ds.getValue(ProductItem.class);
                            if (item!=null){
                                productItems.add(item);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: error -> "+error.getMessage());
                    }
                });
        mutableProducts.postValue(productItems);
        return mutableProducts;
    }

}
