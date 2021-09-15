package com.example.autobot1.activities.landing.frags;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.autobot1.R;
import com.example.autobot1.activities.landing.viewmodels.MechanicShopsViewModel;
import com.example.autobot1.databinding.CustomMapDialogBinding;
import com.example.autobot1.databinding.FragmentBookingBinding;
import com.example.autobot1.databinding.FragmentMapBinding;
import com.example.autobot1.models.Request;
import com.example.autobot1.models.ShopItem;
import com.example.autobot1.models.User;
import com.example.autobot1.notification.APIService;
import com.example.autobot1.notification.Client;
import com.example.autobot1.notification.Data;
import com.example.autobot1.notification.MyResponse;
import com.example.autobot1.notification.Sender;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements RoutingListener, OnMapReadyCallback {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String UID = "uid";
    public static final String TAG = "Map fragment";
    private FragmentMapBinding binding;
    private FusedLocationProviderClient client;
    private SupportMapFragment fragment;
    protected MechanicShopsViewModel viewModel;
    private List<ShopItem> shops;
    private GoogleMap map;
    private double latitude;
    private double longitude;
    private User mUser;
    private String uid;
    private LatLng start;
    private LatLng end;
    private View view;
    private GeoApiContext geoApiContext;
    private Request booking;
    private List<Polyline> polyLines = null;
    private List<ShopItem> shopItemListGlobal;
    private APIService apiService;
    private List<ShopItem> shopItemList;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String latitude, String longitude, String uid) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, latitude);
        args.putString(LONGITUDE, longitude);
        args.putString(UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MechanicShopsViewModel.class);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        shopItemList = new ArrayList<>();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        getTypeOfUser(FirebaseAuth.getInstance().getUid());
        if (getArguments() != null) {
            latitude = getArguments().getDouble(LATITUDE);
            longitude = getArguments().getDouble(LONGITUDE);
            uid = getArguments().getString(UID);
            end = new LatLng(latitude, longitude);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        view = inflater.inflate(R.layout.fragment_map, container, false);
        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    start = new LatLng(location.getLatitude(), location.getLongitude());
                    fragment.getMapAsync(this);
                    if (geoApiContext == null) {
                        geoApiContext = new GeoApiContext.Builder()
                                .apiKey(String.valueOf(R.string.google_maps_key))
                                .build();
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 800);
        }
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(start)
                .title("My position")
                .snippet("Iam here");
        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 10));
        getShopsAround();
        populateMap();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        requireActivity().getMenuInflater().inflate(R.menu.map_view_search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_map);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                List<ShopItem> searches = new ArrayList<>();
                shopItemListGlobal.forEach(shopItem -> {
                    if (shopItem.getTitle().contains(newText) || shopItem.getDescription().contains(newText) || shopItem.getContact().contains(newText)
                            || String.valueOf(shopItem.getLatitude()).contains(newText) || String.valueOf(shopItem.getLongitude()).contains(newText)) {
                        searches.add(shopItem);
                    }
                });
                populateMapSearch(searches);
                return true;
            }
        });
    }

    private void getTypeOfUser(String uid) {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User user1 = user.getValue(User.class);
                            if (user1 != null) {
                                if (user1.getUid().equals(uid)) {
                                    mUser = user1;
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: getTypeOfUser -> " + error.getMessage());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateMap() {
        if (shopItemList.isEmpty()) {
            Toast.makeText(requireContext(), "Clients not found", Toast.LENGTH_SHORT).show();
        } else {
            for (ShopItem shop : shops) {
                if (shop != null) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    shop.getLatitude(),
                                    shop.getLongitude()
                            ))
                            .title(shop.getTitle())
                            .snippet(shop.getDescription()));
                    map.setOnInfoWindowClickListener(marker -> {
                        LatLng pos = marker.getPosition();
                        shops.forEach(shop1 -> {
                            if (new LatLng(shop1.getLatitude(), shop1.getLongitude()).equals(pos)) {
                                end = pos;
                                View v = LayoutInflater.from(requireContext()).inflate(R.layout.custom_map_dialog, null, false);
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                CustomMapDialogBinding binding = CustomMapDialogBinding.bind(v);
                                binding.mapDialogTitleTv.setText(shop1.getTitle());
                                binding.mapDialogDescriptionTv.setText(shop1.getDescription());
                                binding.mapDialogEmailBtn.setOnClickListener(view1 -> {
                                    Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
                                    intent.putExtra("email_address", shop1.getContact());
                                    startActivity(intent);
                                });
                                binding.mapDialogCallBtn.setOnClickListener(view12 -> {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.putExtra("number", shop1.getContact());
                                    startActivity(intent);
                                });
                                binding.mapDialogDirectionBtn.setOnClickListener(view13 -> findRoutes(start, end));
                                builder.setCancelable(true);
                                builder.setView(v);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    });
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateMapSearch(List<ShopItem> shopItemList) {
        shops = shopItemList;
        if (shops.isEmpty()) {
            Toast.makeText(requireContext(), "Clients not found", Toast.LENGTH_SHORT).show();
        } else {
            for (ShopItem shop : shops) {
                if (shop != null) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    shop.getLatitude(),
                                    shop.getLongitude()
                            ))
                            .title(shop.getTitle())
                            .snippet(shop.getDescription()));
                    map.setOnInfoWindowClickListener(marker -> {
                        LatLng pos = marker.getPosition();
                        shops.forEach(shop1 -> {
                            if (new LatLng(shop1.getLatitude(), shop1.getLongitude()).equals(pos)) {
                                end = pos;
                                View v = LayoutInflater.from(requireContext()).inflate(R.layout.custom_map_dialog, null, false);
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                CustomMapDialogBinding binding = CustomMapDialogBinding.bind(v);
                                binding.mapDialogTitleTv.setText(shop1.getTitle());
                                binding.mapDialogDescriptionTv.setText(shop1.getDescription());
                                binding.mapDialogEmailBtn.setOnClickListener(view1 -> {
                                    Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
                                    intent.putExtra("email_address", shop1.getContact());
                                    startActivity(intent);
                                });
                                binding.mapDialogCallBtn.setOnClickListener(view12 -> {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.putExtra("number", shop1.getContact());
                                    startActivity(intent);
                                });
                                binding.mapDialogDirectionBtn.setOnClickListener(view13 -> {
                                    findRoutes(start, end);
                                    Data data = new Data(FirebaseAuth.getInstance().getUid(), "New request from " + mUser.getName(), "Autobot", mUser.getDeviceToken(), R.mipmap.launcher_icon);
                                    Sender sender = new Sender(data, mUser.getDeviceToken());
                                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body().success != -1) {
                                                    Toast.makeText(requireContext(), "Failed to send notification...", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                            Log.e(TAG, "onFailure: -> " + t.getMessage());
                                        }
                                    });
                                });
                                builder.setCancelable(true);
                                builder.setView(v);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    });
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getShopsAround() {
        FirebaseDatabase.getInstance().getReference("shops")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            ShopItem shopItem = ds.getValue(ShopItem.class);
                            if (shopItem!=null){
                                shopItemList.add(shopItem);
                                Log.i(TAG, "getShopsAround: shop -> "+shopItem.toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: error -> "+error.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 800 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    fragment.getMapAsync(googleMap -> {
                        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                        start = pos;
                        Log.i(TAG, "onMapReady: lat:" + location.getLatitude() + " long:" + location.getLongitude());
                        MarkerOptions options = new MarkerOptions();
                        options.position(pos);
                        options.title("My position");
                        options.snippet("Iam here");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
                        googleMap.addMarker(options);
                        getTypeOfUser(FirebaseAuth.getInstance().getUid());
                    });
                }
            });
        }
    }

    public void setBooking(Request booking) {
        this.booking = booking;
    }


    public void findRoutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Snackbar.make(binding.getRoot(), "Unable to get location", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getResources().getString(R.string.google_maps_key))
                    .build();
            routing.execute();
        }
    }

    //Routing callback functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = view.findViewById(R.id.parent_layout);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
        findRoutes(start, end);
    }


    @Override
    public void onRoutingStart() {
        Snackbar.make(binding.getRoot(), "Routing...", Snackbar.LENGTH_LONG).setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {
        CameraUpdateFactory.newLatLngZoom(start, 15);
        if (polyLines != null) {
            polyLines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;
        polyLines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(arrayList.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polyLines.add(polyline);

            } else {
                Toast.makeText(requireContext(), "Rerouting...", Toast.LENGTH_SHORT).show();
            }
        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        if (polylineStartLatLng != null) {
            startMarker.position(polylineStartLatLng);
        }
        startMarker.title("My Location");
        map.addMarker(startMarker);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 10));

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        if (polylineEndLatLng != null) {
            endMarker.position(polylineEndLatLng);
        }
        endMarker.title("Destination");
        map.addMarker(endMarker);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(end, 10));
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(start, end);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}