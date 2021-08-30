package com.example.autobot1.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.autobot1.models.Notification;
import com.example.autobot1.models.ProductItemCart;

@Database(entities = {ProductItemCart.class, Notification.class},version = 1,exportSchema = false)
public abstract class DataBaseInstance extends RoomDatabase {
    private static DataBaseInstance instance;
    public abstract RoomDataBaseDao getRoomDataBaseDao();
    public abstract CartDao getCartDao();
    public static DataBaseInstance getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context,DataBaseInstance.class,"Autobot_DataBase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
