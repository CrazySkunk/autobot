package com.example.autobot1.activities.mechanics.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.autobot1.models.Request;
import com.example.autobot1.models.ScheduleItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingsViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Request>> bookings;
    private Context context;
    private static final String TAG = "BookingsViewModel";
    public BookingsViewModel(Application application){
        super(application);
        bookings = new MutableLiveData<List<Request>>();
        context = application.getBaseContext();
    }
    public LiveData<List<Request>> getMechanicBookings(String uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("mechanics/"+uid+"/bookings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Request> b = new ArrayList<>();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Request booking = ds.getValue(Request.class);
                    b.add(booking);
                    bookings.postValue(b);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "onCancelled: getMechanicBookings -> "+error.getMessage());
            }
        });
        return bookings;
    }
    public LiveData<List<Request>> getClientsBookings(String uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("clients/"+uid+"/bookings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Request> b = new ArrayList<>();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Request booking = ds.getValue(Request.class);
                    b.add(booking);
                    bookings.postValue(b);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "onCancelled: getMechanicBookings -> "+error.getMessage());
            }
        });
        return bookings;
    }
    public void addScheduleMechanic(ScheduleItem scheduleItem,String uid){
        FirebaseDatabase.getInstance().getReference("mechanics/"+uid+"/bookings/"+System.currentTimeMillis())
                .setValue(scheduleItem)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.isComplete()){
                        Toast.makeText(context, "The schedule was added...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
