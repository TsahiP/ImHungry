package com.example.imhungry3.Activity.WorkShop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imhungry3.BaseActivity;
import com.example.imhungry3.Class.MyToast;
import com.example.imhungry3.R;
import com.example.imhungry3.Adapter.AdapterRecyclerWorkshops;
import com.example.imhungry3.Class.WorkShopClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class WorkShopActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    //=================recycaler

    private RecyclerView recyclerView;
    private AdapterRecyclerWorkshops adapter;
    private List<WorkShopClass> workShops;
    //=============recycaler
    private static final String TAG = "";
//    AdapterWorkShop adapterWorkShop;

    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private SlidingUpPanelLayout layout;
    private GoogleMap map;
    private SearchView searchView;
    private ImageButton backPage;
    //    FireBase =======================================
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private StorageReference Ref;
    private ProgressDialog loader;
    Boolean flag;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshop);
        backPage = findViewById(R.id.back_workshop2);
        backPage.setOnClickListener(this);
        loader = new ProgressDialog(this, R.style.MyAlertDialogStyle);
//            loader.setCancelable(false);
        loader.setMessage("Loading...");
        loader.setContentView(R.layout.layour_loader);
        loader.show();


        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        searchView = findViewById(R.id.sv_search);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();

                flag = true;

                if (query != null || !query.equals("")) {

                    try {
                        LatLng latLng = getLocationFromAddress(getBaseContext(), location);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } catch (Throwable e) {
                        MyToast.showShort(getApplicationContext(), "Location is incorrect");
                        e.printStackTrace();
                    }


                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
//        listWorkShop = (ListView) findViewById(R.id.listWorkShop);
//      Recycler
        recyclerView = (RecyclerView) findViewById(R.id.rv_xml);

        workShops = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false
        );
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void onMapReady(GoogleMap googleMap) {
        // [START_EXCLUDE silent]
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        // [END_EXCLUDE]

        map = googleMap;
        layout = (SlidingUpPanelLayout) findViewById(R.id.slidingUp);
        adapter = new AdapterRecyclerWorkshops(this, workShops, this, map, layout);
        recyclerView.setAdapter(adapter);

        addWorkShopToList();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 8));
                        }
                    }
                });
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                loader.dismiss();
            }
        }, 1000);

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    public void addWorkShopToList() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("classess");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("\n\ntest: ", "" + ds.getKey().toString() + "\n" + ds.child("ClassName").getValue());

                    if (!ds.getKey().equals("lastClassId")) {

                        WorkShopClass wk = new WorkShopClass();
                        wk.setWkName(ds.child("ClassName").getValue().toString());
                        wk.setWkDescraption(ds.child("Descraptions").getValue().toString());
                        wk.setWkPrice(ds.child("Price").getValue().toString() + " ₪ ");
                        wk.setWkDate(ds.child("Date").getValue().toString());
                        wk.setWkLocation(ds.child("Location").getValue().toString());
//                        String location = ds.child("Location").getValue().toString();
                        wk.setWkEndTime(ds.child("EndTime").getValue().toString());
                        wk.setWkStartTime(ds.child("StartTime").getValue().toString());
                        wk.setWkUsrEmail(ds.child("email").getValue().toString());
                        wk.setWkRegisterd(ds.child("Registerd").getValue().toString());
                        wk.setWkMaxGuests(ds.child("MaxGuests").getValue().toString());
                        wk.setWkIdUser(ds.child("id").getValue().toString());
                        wk.setWkWhatPrepare(ds.child("whatPrepear").getValue().toString());
                        String id = ds.getKey();
                        wk.setWkId(id);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date();
                        if (date.after(wk.getDateTime())) {
                            DeleteWk(wk.getWkId());
                            continue;
                        }


//                        WorkShop wk = new WorkShop(title, date, description, price,id);
                        workShops.add(wk);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.iconmap));

                        markerOptions.title(wk.getWkName());
                        markerOptions.snippet(wk.getWkPrice() + "₪ " + wk.getWkDate());
                        try {
                            map.addMarker(markerOptions.position(getLocationFromAddress(getBaseContext(), wk.getWkLocation())));
                        } catch (Throwable e) {

                        }


                    }

                }
                Collections.sort(workShops);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void DeleteWk(String id) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("classess").child(id);
        databaseReference.removeValue();
    }


    @Override
    public void onClick(View v) {
        if (v == backPage) {
            onBackPressed();
        }
    }
}

