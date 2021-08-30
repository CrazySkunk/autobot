package com.example.autobot1.db;

import androidx.lifecycle.LiveData;

import com.example.autobot1.models.ProductItemCart;
import com.example.autobot1.models.ShopItemFav;

import java.util.List;

public class FavoriteRepository {
    private final FavoriteDao favoriteDao;

    public FavoriteRepository(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }
    public LiveData<List<ShopItemFav>> getAllCartItems(){
        return favoriteDao.getAllCartProducts();
    }
    public void addProduct(ShopItemFav productItemCart){
        favoriteDao.addProduct(productItemCart);
    }
    public void updateCartItem(ShopItemFav productItemCart){
        favoriteDao.updateCartProduct(productItemCart);
    }

    public void deleteCartProduct(ShopItemFav productItemCart){
        favoriteDao.deleteCartProduct(productItemCart);
    }

    public void deleteAllCartProducts(){
        favoriteDao.deleteAllCartProducts();
    }
}
