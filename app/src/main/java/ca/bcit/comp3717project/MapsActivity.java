package ca.bcit.comp3717project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.SearchView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private float defaultZoomLevel = 12.0f;
    private boolean animationInProgress = false;
    private FirebaseDatabase database;
    private Location lastKnownLocation;
    static private List<Restaurant> RestaurantList;
    private Button btnSettings;
    private SearchView svMap;
    private LatLng currLocat;
    private ArrayList<Restaurant> markersRestaurantMapList;
    private BottomNavigationView mNavBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Read from the database
        RestaurantList = MainActivity.restaurantList;
        markersRestaurantMapList = new ArrayList<Restaurant>();
        svMap = findViewById(R.id.svMap);
        mNavBar = findViewById(R.id.menu_navBar);
        mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(this, mNavBar));
        svMap.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String restaurant = svMap.getQuery().toString();

                List<Restaurant> restSerachList = RestaurantList.stream()
                        .filter(p -> p.getNAME().toLowerCase().startsWith(restaurant.toLowerCase())).collect(Collectors.toList());
                populateMarksOnMapBySerach(restSerachList);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        Button buttonMinus = (Button)findViewById( R.id.btnZoomOut );
        Button buttonPlus = (Button)findViewById( R.id.btnZoomIn );
        buttonMinus.setTypeface(font);
        buttonPlus.setTypeface(font);

        btnSettings =  findViewById(R.id.btnMapSetting);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                showMapSettingDialog();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationClickListener(this);
        onCurrentLocation();
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("restaurants");
        //get current location.


        // Add a marker in Sydney and move the camera

        currLocat = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocat,defaultZoomLevel));
        populateMarksOnMapByDistance(9, 2);

    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("marker info: "+marker);
        for(Restaurant r : markersRestaurantMapList){
            if(marker.getTitle().equals(r.getNAME())){

                Intent intent = new Intent(MapsActivity.this, DetailActivity.class);
                intent.putExtra("name",r.getNAME());
                intent.putExtra("address",r.getPHYSICALADDRESS());
                intent.putExtra("city",r.getPHYSICALCITY());
                intent.putExtra("rating",r.getHazardRating());
                intent.putExtra("date",r.getInspectionDate());
                intent.putExtra("critical",r.getNumCritical());
                intent.putExtra("noncritical",r.getNumNonCritical());
                intent.putExtra("latitude",r.getLATITUDE());
                intent.putExtra("longitude",r.getLONGITUDE());
                startActivity(intent);
            }
        }
    }
    private void populateMarksOnMapBySerach(List<Restaurant> restSerachList){
        int count = 25;
        if(markersRestaurantMapList != null){
            markersRestaurantMapList.clear();
        }
        mMap.clear();
        for(Restaurant r : restSerachList){
            markersRestaurantMapList.add(r);
            if(count == 0){
                break;
            }
            LatLng loc = new LatLng(Float.valueOf(r.getLATITUDE()),Float.valueOf(r.getLONGITUDE()));
            findDistanceNearByRestaurant(loc);
            addMarkerMap(loc,r.getNAME(),findDistanceNearByRestaurant(loc),r.getHazardRating());
            count --;

        }
    }

    private void populateMarksOnMapByDistance(int numberRest, double distanceTravel){
        DecimalFormat df = new DecimalFormat("#.##");
        LatLng testRestaurant;
        String restName = "";
        double longDist = 0;
        int counter = 1;
        if(markersRestaurantMapList != null){
            markersRestaurantMapList.clear();
        }
        for(Restaurant rest : RestaurantList) {
            if(counter > numberRest){
                break;
            }
            if (rest.getLATITUDE() != "#N/A" || rest.getLONGITUDE() != "#N/A" || rest.getNAME() != "#N/A") {
                testRestaurant = new LatLng(Double.valueOf(rest.getLATITUDE()),
                        Double.valueOf(rest.getLONGITUDE()));

                if (findDistanceNearByRestaurant(testRestaurant) < distanceTravel ) {
                    addMarkerMap(testRestaurant, rest.getNAME(),
                            findDistanceNearByRestaurant(testRestaurant),rest.getHazardRating());
                    counter++;
                    markersRestaurantMapList.add(rest);
                    if(findDistanceNearByRestaurant(testRestaurant) > longDist){
                        longDist = findDistanceNearByRestaurant(testRestaurant);
                        restName = rest.getNAME();
                    }
                }
            }

        }
        Toast.makeText(MapsActivity.this,
                restName +" Distance "+  df.format(longDist) + " km",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "You are here", Toast.LENGTH_LONG).show();
    }

    public void onSearch(View v) {
        List<Address> addressList = null;
        EditText editTextLocation = (EditText) findViewById(R.id.svRestaurant);
        String location = editTextLocation.getText().toString();
        if (location != null && location != "") {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address adr = addressList.get(0);
            LatLng latLng = new LatLng(adr.getLatitude(), adr.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    GoogleMap.CancelableCallback cancelableCallback = new GoogleMap.CancelableCallback() {
        @Override
        public void onFinish() {
            animationInProgress = false;
        }

        @Override
        public void onCancel() {
            animationInProgress = false;
        }
    };

    public void onChangeMapType(View v) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void onZoom(View v) {
        if (v.getId() == R.id.btnZoomIn)
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        else mMap.animateCamera(CameraUpdateFactory.zoomOut());
    }
    // get the current location
    public void onCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lastKnownLocation = MainActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }
    //add markers to the map
    private void addMarkerMap(LatLng coordinates, String title, double dist, String rating) {
        DecimalFormat df = new DecimalFormat("#.##");
        if(title == null) {
            title = "Current Location: %4.3f Lat %4.3f Long.";
        }
        String msg = String.format(title, coordinates.latitude, coordinates.longitude);
        float color;
        if("Low".equals(rating)){
            color = BitmapDescriptorFactory.HUE_GREEN;
        } else if("Moderate".equals(rating)){
            color = BitmapDescriptorFactory.HUE_ORANGE;
        } else {
            color = BitmapDescriptorFactory.HUE_RED;
        }

        LatLng latlng = new LatLng(coordinates.latitude, coordinates.longitude);
        mMap.addMarker(new MarkerOptions().position(latlng).title(msg)
                .snippet(df.format(dist) + " km")
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
    }


    private float findDistanceNearByRestaurant(LatLng coordinates){
        double latX = lastKnownLocation.getLatitude();
        double lonY = lastKnownLocation.getLongitude();
        Location loc1 = new Location("");
        loc1.setLatitude(latX);
        loc1.setLongitude(lonY);

        Location loc2 = new Location("");
        loc2.setLatitude(coordinates.latitude);
        loc2.setLongitude(coordinates.longitude);

        return loc1.distanceTo(loc2) / 1000;

    }
    //open the dialog box
    private void showMapSettingDialog(){


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.setting_dialog, null);
        final Button btnWalk = dialogView.findViewById(R.id.btnWalkMap);
        final Button btnBike = dialogView.findViewById(R.id.btnBikeMap);
        final Button btnDrive = dialogView.findViewById(R.id.btnDriveMap);
        final EditText etNumRestaurants = dialogView.findViewById(R.id.etNumberRestaurants);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Map Settings");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                int numRest;

                etNumRestaurants.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "25")});
                if(!etNumRestaurants.getText().toString().equals("")) {
                    numRest =  Integer.parseInt(etNumRestaurants.getText().toString());
                    populateMarksOnMapByDistance(numRest,2);
                    mMap.addCircle(new CircleOptions()
                            .center(currLocat)
                            .radius(2000)
                            .strokeColor(Color.rgb(166, 48, 199)));
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(MapsActivity.this,
                            "Please enter number of restaurants",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                int numRest;

                etNumRestaurants.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "25")});
                if(!etNumRestaurants.getText().toString().equals("")) {
                    numRest =  Integer.parseInt(etNumRestaurants.getText().toString());
                    populateMarksOnMapByDistance(numRest,4);
                    mMap.addCircle(new CircleOptions()
                            .center(currLocat)
                            .radius(4000)
                            .strokeColor(Color.rgb(166, 48, 199)));
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(MapsActivity.this,
                            "Please enter number of restaurants",Toast.LENGTH_LONG).show();
                }
            }
        });
        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                int numRest;

                etNumRestaurants.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "25")});
                if(!etNumRestaurants.getText().toString().equals("")) {
                    numRest =  Integer.parseInt(etNumRestaurants.getText().toString());
                    populateMarksOnMapByDistance(numRest,6);
                    mMap.addCircle(new CircleOptions()
                            .center(currLocat)
                            .radius(6000)
                            .strokeColor(Color.rgb(166, 48, 199)));
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(MapsActivity.this,
                            "Please enter number of restaurants",Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}













