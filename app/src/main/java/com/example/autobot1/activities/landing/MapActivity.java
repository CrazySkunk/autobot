package com.example.autobot1.activities.landing;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.frags.MapFragment;
import com.example.autobot1.activities.landing.viewmodels.MechanicShopsViewModel;
import com.example.autobot1.activities.mechanics.frags.MechanicShopsFragment;
import com.example.autobot1.activities.mechanics.frags.ScheduleFragment;
import com.example.autobot1.activities.landing.frags.SpecificShopFragment;
import com.example.autobot1.databinding.ActivityMapBinding;
import com.google.android.material.navigation.NavigationView;

public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.autobot1.databinding.ActivityMapBinding binding = ActivityMapBinding.inflate(getLayoutInflater());
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(binding.getRoot());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout,new MapFragment())
                .commit();
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navDrawer;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_opened, R.string.drawer_closed);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavController navigation = Navigation.findNavController(this,R.id.frame_layout);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.recent) {
            inflateFragContainer(new MechanicShopsFragment());
        }else if (item.getItemId() == R.id.schedule) {
            inflateFragContainer(new ScheduleFragment());
        }else if (item.getItemId() == R.id.favorites) {
            inflateFragContainer(new MechanicShopsFragment());
        }else if (item.getItemId()==R.id.shop_parts){
            inflateFragContainer(new SpecificShopFragment());
        }else if (item.getItemId()==R.id.map){
            inflateFragContainer(new MapFragment());
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void inflateFragContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,
                        fragment)
                .commit();
    }
}