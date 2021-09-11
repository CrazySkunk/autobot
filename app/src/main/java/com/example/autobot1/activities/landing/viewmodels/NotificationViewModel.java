package com.example.autobot1.activities.landing.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.autobot1.models.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    private static final String TAG = "NotificationViewModel";
    private final List<Notification> notifications;
    private final MutableLiveData<List<Notification>> notifLiveData;
    public NotificationViewModel(@NonNull Application application) {
        super(application);
        notifications = new ArrayList<>();
        notifLiveData = new MutableLiveData<>();
    }
    public LiveData<List<Notification>>getNotification(String uid){
        FirebaseDatabase.getInstance().getReference("notification/"+uid)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(notification->{
                            Notification notif = notification.getValue(Notification.class);
                            if (notif!=null){
                                notifications.add(notif);
                            }
                        });
                        notifLiveData.postValue(notifications);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: -> "+error.getMessage());
                    }
                });
        return notifLiveData;
    }

}
