package com.example.autobot1.db;

import androidx.lifecycle.LiveData;

import com.example.autobot1.models.RecentShopItem;

import java.util.List;

public class RecentShopsRepository {
    private RecentShopDao recentShopDao;

    public RecentShopsRepository(RecentShopDao recentShopDao) {
        this.recentShopDao = recentShopDao;
    }
    public void addRecentShop(RecentShopItem recentShopItem){
        recentShopDao.addRecentShop(recentShopItem);
    }

    public LiveData<List<RecentShopItem>>getRecentShops(){
        return recentShopDao.getRecentShops();
    }

    public void deleteRecentShop(RecentShopItem recentShopItem){
        recentShopDao.deleteRecentShop(recentShopItem);
    }

    public void deleteAllRecentShops(){
        recentShopDao.deleteAll();
    }
}
