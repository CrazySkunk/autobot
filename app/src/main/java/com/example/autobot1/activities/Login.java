package com.example.autobot1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.example.autobot1.R;

public class Login extends AppCompatActivity {

    RelativeLayout rellay1, rellay2;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}