package com.example.autobot1.activities.splashScreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autobot1.R;
import com.example.autobot1.activities.credentials.CredentialsActivity;
import com.example.autobot1.activities.landing.MapActivity;
import com.example.autobot1.activities.mechanics.MechanicsActivity;
import com.example.autobot1.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    //Variables
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.imageView2);
        logo = findViewById(R.id.textView3);
        slogan = findViewById(R.id.textView5);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        int SPLASH_SCREEN = 5000;
        new Handler().postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                startActivity(new Intent(SplashScreen.this, CredentialsActivity.class));
            } else {
                FirebaseDatabase.getInstance().getReference("users")
                        .addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getChildren().forEach(user -> {
                                    User u = user.getValue(User.class);
                                    if (u != null) {
                                        if (u.getUid().equals(FirebaseAuth.getInstance().getUid()) && u.getAccountType().equals("Mechanic")) {
                                            startActivity(new Intent(SplashScreen.this, MechanicsActivity.class));
                                        } else {
                                            startActivity(new Intent(SplashScreen.this, MapActivity.class));
                                        }
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.i(TAG, "onCancelled: -> " + error.getMessage());
                            }
                        });
            }
        }, SPLASH_SCREEN);

    }
}