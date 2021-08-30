package com.example.autobot1.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.autobot1.models.ShopItem;
import com.example.autobot1.models.User;

import java.util.List;

@Dao
public interface RoomDataBaseDao {
    @Insert
    Long insert(User user);

    @Insert
    Long addShop(ShopItem shop);

    @Query("SELECT * FROM Favorite_shops")
    List<ShopItem> getFavShops();

    @Query("DELETE FROM FAVORITE_SHOPS WHERE title LIKE title")
    void deleteFavShop(String mTitle);
}
