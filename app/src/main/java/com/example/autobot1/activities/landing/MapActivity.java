package com.example.autobot1.activities.landing;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobot1.R;
import com.example.autobot1.activities.credentials.viewmodels.CredentialsViewModel;
import com.example.autobot1.activities.landing.frags.FavoritesFragment;
import com.example.autobot1.activities.landing.frags.MapFragment;
import com.example.autobot1.activities.landing.frags.RecentFragment;
import com.example.autobot1.activities.landing.frags.SpecificShopFragment;
import com.example.autobot1.activities.mechanics.frags.ScheduleFragment;
import com.example.autobot1.databinding.ActivityMapBinding;
import com.example.autobot1.databinding.HeaderLayoutBinding;
import com.example.autobot1.models.AccountType;
import com.example.autobot1.models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MapActivity";
    private ActivityMapBinding binding;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    private List<AccountType> accountType;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CredentialsViewModel credentialsViewModel = new ViewModelProvider(this).get(CredentialsViewModel.class);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        accountType = credentialsViewModel.getAccountType();
        type = accountType.get(0).getAccountType();
        Log.i(TAG, "onCreate: accountType -> " + type);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout, new MapFragment())
                .commit();
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navDrawer;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_opened, R.string.drawer_closed);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getUser();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.recent) {
            inflateFragContainer(new RecentFragment());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Recently viewed shops");
        } else if (item.getItemId() == R.id.schedule) {
            inflateFragContainer(new ScheduleFragment());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Schedules");
        } else if (item.getItemId() == R.id.favorites) {
            inflateFragContainer(new FavoritesFragment());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Favorite shops");
        } else if (item.getItemId() == R.id.shop_parts) {
            inflateFragContainer(new SpecificShopFragment());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Mechanic shops");
        } else if (item.getItemId() == R.id.map) {
            inflateFragContainer(new MapFragment());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Map");
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getUser() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User user1 = user.getValue(User.class);
                            if (user1 != null) {
                                if (user1.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                    View view = binding.navDrawer.getHeaderView(0);
                                    HeaderLayoutBinding hBinding = HeaderLayoutBinding.bind(view);
                                    hBinding.nameHeaderLayout.setText(user1.getName());
                                    hBinding.emailHeaderLayout.setText(user1.getEmail());
                                    Picasso.get().load(user1.getImageUrl()).placeholder(R.drawable.account_circle).into(hBinding.imageView);
                                }
                            } else {
                                Toast.makeText(MapActivity.this, "We cant find you in database", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: error -> " + error.getMessage());
                    }
                });
    }

    private void inflateFragContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,
                        fragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}