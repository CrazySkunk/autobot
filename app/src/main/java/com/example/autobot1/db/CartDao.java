package com.example.autobot1.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.autobot1.models.ProductItemCart;

import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addProduct(ProductItemCart productItemCart);

    @Query("SELECT * FROM `Cart-Products` ORDER BY id ASC")
    public LiveData<List<ProductItemCart>> getAllCartProducts();

    @Update
    public void updateCartProduct(ProductItemCart productItemCart);

    @Delete
    public void deleteCartProduct(ProductItemCart productItemCart);

    @Query("DELETE FROM `Cart-Products`")
    public void deleteAllCartProducts();

}
