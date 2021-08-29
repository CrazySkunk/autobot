package com.example.autobot1.activities.mechanics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.autobot1.R;
import com.example.autobot1.activities.credentials.frags.RegisterShopFragment;
import com.example.autobot1.activities.landing.frags.BookingFragment;
import com.example.autobot1.activities.landing.frags.MapFragment;
import com.example.autobot1.activities.mechanics.frags.MechanicShopsFragment;
import com.example.autobot1.activities.mechanics.frags.NotificationsFragment;
import com.example.autobot1.activities.mechanics.frags.ScheduleFragment;
import com.example.autobot1.activities.mechanics.models.Bookings;
import com.example.autobot1.activities.mechanics.models.FragComponent;
import com.example.autobot1.adapters.MechanicPageAdapter;
import com.example.autobot1.databinding.ActivityMechanicsBinding;
import com.example.autobot1.models.Request;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;

public class MechanicsActivity extends AppCompatActivity implements BookingFragment.SendMessage {
    private ActivityMechanicsBinding binding;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle saveInstanceBundle) {
        super.onCreate(saveInstanceBundle);
        binding = ActivityMechanicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        viewPager = binding.mechanicsViewPager;
        binding.tabLayout.setSelectedTabIndicatorColor(Color.GRAY);
        setUpAdapter();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getId() == R.id.shop_tab) {
                    toolbar.setTitle("Shops");
                    binding.tabLayout.selectTab(tab);
                } else if (tab.getId() == R.id.notifications_tab) {
                    toolbar.setTitle("Notifications");
                    binding.tabLayout.selectTab(tab);
                } else if (tab.getId() == R.id.booking_tab) {
                    toolbar.setTitle("Booking");
                    binding.tabLayout.selectTab(tab);
                } else if (tab.getId() == R.id.schedule) {
                    toolbar.setTitle("Schedules");
                    binding.tabLayout.selectTab(tab);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setMapSelected(){
        viewPager.setCurrentItem(3,true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_view_mechanic_menu, menu);
        MenuItem item = menu.findItem(R.id.search_mechanic);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search for client");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.register_shop) {
            View view = LayoutInflater.from(this).inflate(R.layout.register_dialog_fragment, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Register shop")
                    .setView(view)
                    .setCancelable(true)
                    .setPositiveButton("Register", (dialog, which) -> {
                        FirebaseDatabase.getInstance().getReference().setValue(null);
                    });
            AlertDialog alertDialog = builder.create();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_dialog, new RegisterShopFragment())
                    .commit();
            alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpAdapter() {
        MechanicPageAdapter adapter = new MechanicPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragComponent(new MechanicShopsFragment(), "Shops"));
        adapter.addFragment(new FragComponent(new NotificationsFragment(), "Notification"));
        adapter.addFragment(new FragComponent(new BookingFragment(), "Requests"));
        adapter.addFragment(new FragComponent(new ScheduleFragment(), "Schedules"));
        adapter.addFragment(new FragComponent(new MapFragment(), "Maps"));
        viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void sendData(Request booking) {
        String tag = "android:switcher:" + R.id.mechanics_view_pager + ":" + 1;
        MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment!=null){
        fragment.setBooking(booking);}else {
            Toast.makeText(this, "Internal error", Toast.LENGTH_SHORT).show();
        }
    }
}
