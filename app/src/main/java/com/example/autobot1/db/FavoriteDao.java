package com.example.autobot1.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.autobot1.models.ShopItemFav;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addProduct(ShopItemFav shopItemFav);

    @Query("SELECT * FROM `Favorite_shops` ORDER BY id ASC")
    public LiveData<List<ShopItemFav>> getAllCartProducts();

    @Update
    public void updateCartProduct(ShopItemFav shopItemFav);

    @Delete
    public void deleteCartProduct(ShopItemFav shopItemFav);

    @Query("DELETE FROM `Favorite_shops`")
    public void deleteAllCartProducts();
}
