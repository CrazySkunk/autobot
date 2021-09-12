package com.example.autobot1.activities.splashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autobot1.R;
import com.example.autobot1.activities.credentials.CredentialsActivity;
import com.example.autobot1.activities.landing.MapActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IntroScreen extends AppCompatActivity {
    //Variables
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;
    int SPLASH_SCREEN = 3000;

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

        new Handler().postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                startActivity(new Intent(IntroScreen.this, CredentialsActivity.class));
            } else {
                startActivity(new Intent(IntroScreen.this, MapActivity.class));
            }
            finish();
        }, SPLASH_SCREEN);
    }
}