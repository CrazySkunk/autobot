package com.example.autobot1.activities.landing.viewmodels;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.autobot1.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MechanicsViewModel extends AndroidViewModel {
    private static final String TAG = "MechanicsViewModel";
    private MutableLiveData<List<User>> mechanics;
    private List<User> mechanicList;
    public MechanicsViewModel(@NonNull Application application) {
        super(application);
        mechanics = new MutableLiveData<>();
        mechanicList = new ArrayList<>();
    }

    public LiveData<List<User>> getMechanics() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user->{
                            User mUser = user.getValue(User.class);
                            if (mUser!=null){
                                if (mUser.getAccountType().equals("Mechanic")){
                                    mechanicList.add(mUser);
                                }
                            }
                        });
                        mechanics.postValue(mechanicList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: -> "+error.getMessage() );
                    }
                });
        return mechanics;
    }
}
