package com.example.autobot1.activities.landing.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.autobot1.models.ShopItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MechanicShopsViewModel extends AndroidViewModel {
    private static final String TAG = "MechanicShopsViewModel";
   List<ShopItem> shops;
    MutableLiveData<List<ShopItem>> mShops;

    public MechanicShopsViewModel(Application application) {
        super(application);
        shops = new ArrayList<>();
        mShops = new MutableLiveData<>();
    }

    public MutableLiveData<List<ShopItem>> getShops() {
        FirebaseDatabase.getInstance().getReference("shops")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            ShopItem shopItem = ds.getValue(ShopItem.class);
                            if (shopItem!=null){
                                shops.add(shopItem);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: error -> "+error.getMessage());
                    }
                });
        mShops.postValue(shops);
        return mShops;
    }

}
