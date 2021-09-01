package com.example.autobot1.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.autobot1.models.RecentShopItem;

import java.util.List;

@Dao
public interface RecentShopDao {
    @Insert
    public void addRecentShop(RecentShopItem shopItem);

    @Query("SELECT * FROM `Recent-shops-table`")
    public LiveData<List<RecentShopItem>> getRecentShops();

    @Delete
    public void deleteRecentShop(RecentShopItem recentShopItem);

    @Query("DELETE FROM `Recent-shops-table`")
    public void deleteAll();
}
