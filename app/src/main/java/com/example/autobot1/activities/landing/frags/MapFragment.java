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
import com.example.autobot1.activities.credentials.CredentialsActivity;
import com.example.autobot1.activities.landing.viewmodels.MechanicShopsViewModel;
import com.example.autobot1.databinding.CustomMapDialogBinding;
import com.example.autobot1.models.Request;
import com.example.autobot1.models.ShopItem;
import com.example.autobot1.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, RoutingListener {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    public static final String TAG = "Map fragment";
    private FusedLocationProviderClient client;
    private SupportMapFragment fragment;
    protected MechanicShopsViewModel viewModel;
    private List<ShopItem> shops;
    private GoogleMap map;
    private double latitude;
    private double longitude;
    private double mLatitude;
    private double mLongitude;
    private LatLng start;
    private LatLng end;
    private View view;
    private String number;
    private GeoApiContext geoApiContext;
    private Request booking;
    private List<Polyline> polylines = null;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String latitude, String longitude) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, latitude);
        args.putString(LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MechanicShopsViewModel.class);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (getArguments() != null) {
            latitude = getArguments().getDouble(LATITUDE);
            longitude = getArguments().getDouble(LONGITUDE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    fragment.getMapAsync(googleMap -> {
                        map = googleMap;
                        start = new LatLng(location.getLatitude(), location.getLongitude());
                        if (booking == null) {
                            getTypeOfUser(FirebaseAuth.getInstance().getUid(), googleMap, location);
                        } else {
                            pinPoint(booking, location);
                        }
                    });
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
        return view;
    }

    private void getTypeOfUser(String uid, GoogleMap googleMap, Location location) {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User user1 = user.getValue(User.class);
                            if (user1 != null)
                                if (user1.getUid().equals(uid) && user1.getAccountTYpe().equals("Mechanic")) {
                                    populateMechanicMap(user1.getUid(), googleMap, location);
                                } else {
                                    populateUserMap(googleMap, location);
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
    private void populateMechanicMap(String uid, GoogleMap googleMap, Location location) {
        List<Request> bookings = getBookings(uid);
        if (shops.isEmpty()) {
            Toast.makeText(requireContext(), "Clients found", Toast.LENGTH_SHORT).show();
        } else {
            for (Request shop : bookings) {
                if (shop != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    shop.getLocation().latitude,
                                    shop.getLocation().longitude
                            ))
                            .title(shop.getFromName())
                            .snippet("Client"));
                    googleMap.setOnInfoWindowClickListener(marker -> {
                        LatLng pos = marker.getPosition();
                        shops.forEach(shop1 -> {
                            if (shop1.getLocation().equals(pos)) {
                                View v = LayoutInflater.from(requireContext()).inflate(R.layout.custom_map_dialog, null, false);
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                TextView title = v.findViewById(R.id.shop_title_text_view);
                                TextView description = v.findViewById(R.id.map_dialog_description_tv);
                                Button email = v.findViewById(R.id.map_dialog_email_btn);
                                Button call = v.findViewById(R.id.map_dialog_call_btn);
                                Button getDirections = v.findViewById(R.id.map_dialog_direction_btn);
                                title.setText(shop1.getTitle());
                                description.setText(shop1.getDescription());
                                email.setOnClickListener(view1 -> {
                                    Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
                                    intent.putExtra("email_address", shop1.getContact());
                                    startActivity(intent);
                                });
                                call.setOnClickListener(view12 -> {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.putExtra("number", shop1.getContact());
                                    startActivity(intent);
                                });
                                getDirections.setOnClickListener(view13 -> getRoutes());
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
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i(TAG, "onMapReady: lat:" + location.getLatitude() + " long:" + location.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(pos);
        options.title("My position");
        options.snippet("Iam here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
        googleMap.addMarker(options);
        googleMap.setOnInfoWindowClickListener(this);
    }

    private List<Request> getBookings(String uid) {
        List<Request> bookings = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("booking-request/" + uid)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(request -> {
                            Request booking = request.getValue(Request.class);
                            bookings.add(booking);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: getBookings -> " + error.getMessage());
                    }
                });
        return bookings;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateUserMap(GoogleMap googleMap, Location location) {
        shops = getShopsAround();
        if (shops.isEmpty()) {
            Toast.makeText(requireContext(), "No shops found", Toast.LENGTH_SHORT).show();
        } else {
            for (ShopItem shop : shops) {
                if (shop != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    shop.getLocation().getLatitude(),
                                    shop.getLocation().getLongitude()
                            ))
                            .title(shop.getTitle())
                            .snippet(shop.getDescription().substring(0, 10)));
                    googleMap.setOnInfoWindowClickListener(marker -> {
                        LatLng pos = marker.getPosition();
                        shops.forEach(shop1 -> {
                            if (shop1.getLocation().equals(pos)) {
                                View v = LayoutInflater.from(requireContext()).inflate(R.layout.custom_map_dialog, null, false);
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                TextView title = v.findViewById(R.id.shop_title_text_view);
                                TextView description = v.findViewById(R.id.map_dialog_description_tv);
                                Button email = v.findViewById(R.id.map_dialog_email_btn);
                                Button call = v.findViewById(R.id.map_dialog_call_btn);
                                Button getDirections = v.findViewById(R.id.map_dialog_direction_btn);
                                getDirections.setVisibility(View.GONE);
                                title.setText(shop1.getTitle());
                                description.setText(shop1.getDescription());
                                email.setOnClickListener(view1 -> {
                                    Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
                                    intent.putExtra("email_address", shop1.getContact());
                                    startActivity(intent);
                                });
                                call.setOnClickListener(view12 -> {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.putExtra("number", shop1.getContact());
                                    startActivity(intent);
                                });
                                getDirections.setOnClickListener(view13 -> getRoutes());
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
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i(TAG, "onMapReady: lat:" + location.getLatitude() + " long:" + location.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(pos);
        options.title("My position");
        options.snippet("Iam here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
        googleMap.addMarker(options);
        googleMap.setOnInfoWindowClickListener(this);
    }

    private void getRoutes() {
        //todo: fetch routes from directions api
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireContext(), CredentialsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /***
     * @author jamie@fortnox we will handle the search queries here
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        requireActivity().getMenuInflater().inflate(R.menu.map_view_client_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search by name or character...");
        searchView.setPadding(10, 0, 10, 0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<ShopItem> getShopsAround() {
        List<ShopItem> shopItemList = new ArrayList<>();
        viewModel.getShops().observe(getViewLifecycleOwner(), shopItems ->
                shopItems.forEach(shopItem -> {
                    if (shopItem.getLocation().getLatitude() == latitude + 0.5 && shopItem.getLocation().getLongitude() == longitude + 0.5) {
                        shopItemList.add(shopItem);
                    } else if (shopItem.getLocation().getLatitude() == latitude + 0.4 && shopItem.getLocation().getLongitude() == longitude + 0.4) {
                        shopItemList.add(shopItem);
                    } else if (shopItem.getLocation().getLatitude() == latitude + 0.3 && shopItem.getLocation().getLongitude() == longitude + 0.3) {
                        shopItemList.add(shopItem);
                    } else if (shopItem.getLocation().getLatitude() == latitude + 0.2 && shopItem.getLocation().getLongitude() == longitude + 0.2) {
                        shopItemList.add(shopItem);
                    } else if (shopItem.getLocation().getLatitude() == latitude + 0.1 && shopItem.getLocation().getLongitude() == longitude + 0.1) {
                        shopItemList.add(shopItem);
                    }
                }));
        return shopItemList;
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
                        getTypeOfUser(FirebaseAuth.getInstance().getUid(), googleMap, location);
                    });
                }
            });
        }
    }

    public void setBooking(Request booking) {
        this.booking = booking;
    }

    private void pinPoint(Request booking, Location location) {
        fragment.getMapAsync(googleMap -> {
            start = new LatLng(location.getLatitude(), location.getLongitude());
            end = new LatLng(booking.getLocation().latitude, booking.getLocation().longitude);
            Log.i(TAG, "onMapReady: lat:" + location.getLatitude() + " long:" + location.getLongitude());
            MarkerOptions options = new MarkerOptions();
            options.position(start);
            options.title("My position");
            options.snippet("Iam here");
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 8));
            MarkerOptions options1 = new MarkerOptions();
            options1.position(end);
            options1.title("Client");
            options1.snippet("They are here");
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(end, 8));
            googleMap.addMarker(options);
            googleMap.addMarker(options1);
            getRoutes();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        if (Objects.equals(marker.getSnippet(), "Iam here") && Objects.equals(marker.getTitle(), "My position")) {
            marker.hideInfoWindow();
        } else {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.custom_map_dialog, null, false);
            CustomMapDialogBinding binding = CustomMapDialogBinding.bind(view);
            binding.mapDialogTitleTv.setText(marker.getTitle());
            binding.mapDialogDescriptionTv.setText(marker.getSnippet());
            binding.mapDialogEmailBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                startActivity(intent);
            });
            binding.mapDialogCallBtn.setOnClickListener(v -> {
                shops.forEach(shopItem -> {
                    if (shopItem.getTitle().equals(marker.getTitle())) {
                        number = shopItem.getContact();
                    }
                });
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.putExtra("number", number);

            });
            binding.mapDialogDirectionBtn.setOnClickListener(v -> {
                shops.forEach(shopItem -> {
                    findRoutes(start, end);
                });
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                    .setView(view)
                    .setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void findRoutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("Your Api Key")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = view.findViewById(R.id.parent_layout);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
        findRoutes(start, end);
    }


    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;

        polylines = new ArrayList<>();
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
                polylines.add(polyline);

            } else {
                Toast.makeText(requireContext(), "Rerouting...", Toast.LENGTH_SHORT).show();
            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        map.addMarker(startMarker);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 10));

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        map.addMarker(endMarker);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 10));

    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(start, end);
    }
}