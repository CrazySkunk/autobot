package com.example.autobot1.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.autobot1.models.AccountType;
import com.example.autobot1.models.Notification;
import com.example.autobot1.models.ProductItemCart;
import com.example.autobot1.models.RecentShopItem;
import com.example.autobot1.models.ShopItemFav;

@Database(entities = {ProductItemCart.class, Notification.class, ShopItemFav.class, AccountType.class, RecentShopItem.class},version = 1,exportSchema = false)
public abstract class DataBaseInstance extends RoomDatabase {
    private static DataBaseInstance instance;
    public abstract CartDao getCartDao();
    public abstract FavoriteDao getFavDao();
    public abstract AccountTypeDao getAccountTypeDao();
    public abstract RecentShopDao getRecentShopsDao();
    public static DataBaseInstance getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context,DataBaseInstance.class,"Autobot_DataBase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
