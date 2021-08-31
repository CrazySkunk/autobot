package com.example.autobot1.activities.landing.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.autobot1.db.DataBaseInstance;
import com.example.autobot1.db.FavoriteDao;
import com.example.autobot1.db.FavoriteRepository;
import com.example.autobot1.models.ShopItemFav;

import java.util.List;

public class FavShopViewModel extends AndroidViewModel {
    final private FavoriteDao favoriteDao;
    final private FavoriteRepository favoriteRepository;
    public FavShopViewModel(@NonNull Application application) {
        super(application);
        favoriteDao = DataBaseInstance.getInstance(application).getFavDao();
        favoriteRepository = new FavoriteRepository(favoriteDao);
    }
    public LiveData<List<ShopItemFav>> getAllShops(){
        return favoriteRepository.getAllCartItems();
    }
    public void addCartItem(ShopItemFav shopItemFav){
        Thread thread = new Thread(() -> favoriteRepository.addProduct(shopItemFav));
        thread.start();
    }
    public void updateShop(ShopItemFav shopItemFav){
        new Thread(()-> favoriteRepository.updateCartItem(shopItemFav));
    }
    public void deleteShop(ShopItemFav shopItemFav){
        new Thread(()->favoriteRepository.deleteCartProduct(shopItemFav));
    }

    public void deleteAllShops(){
        new Thread(favoriteRepository::deleteAllCartProducts);
    }
}
