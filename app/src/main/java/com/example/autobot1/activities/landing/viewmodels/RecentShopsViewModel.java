package com.example.autobot1.activities.landing.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.autobot1.db.DataBaseInstance;
import com.example.autobot1.db.RecentShopDao;
import com.example.autobot1.db.RecentShopsRepository;
import com.example.autobot1.models.RecentShopItem;

import java.util.List;

public class RecentShopsViewModel extends AndroidViewModel {
    private RecentShopDao recentShopDao;
    private final RecentShopsRepository recentShopsRepository;
    public RecentShopsViewModel(@NonNull Application application) {
        super(application);
        recentShopDao = DataBaseInstance.getInstance(application).getRecentShopsDao();
        recentShopsRepository = new RecentShopsRepository(recentShopDao);
    }

    public void addRecentShop(RecentShopItem recentShopItem){
        recentShopsRepository.addRecentShop(recentShopItem);
    }

    public LiveData<List<RecentShopItem>>getAllRecentShops(){
        return recentShopsRepository.getRecentShops();
    }
    public void deleteRecentShop(RecentShopItem recentShopItem){
        recentShopsRepository.deleteRecentShop(recentShopItem);
    }

    public void deleteAllRecentShops(){
        recentShopsRepository.deleteAllRecentShops();
    }
}
