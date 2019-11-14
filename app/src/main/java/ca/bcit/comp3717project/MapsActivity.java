package ca.bcit.comp3717project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;

import android.widget.ListView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnInfoWindowClickListener {

    private final String TAG = "HumbleMaps";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private float defaultZoomLevel = 12.0f;
    private boolean animationInProgress = false;
    private FirebaseDatabase database;
    private Location lastKnownLocation;
    private List<Restaurant> RestaurantList;
    private Button btnSettings;

    private LatLng currLocat;
    private ArrayList<Restaurant> markersRestaurantMapList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Read from the database
        RestaurantList = new ArrayList<Restaurant>();

        markersRestaurantMapList = new ArrayList<Restaurant>();

        setContentView(R.layout.activity_maps);
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("restaurants");
        //get current location.
        onCurrentLocation();

        // Add a marker in Sydney and move the camera

        currLocat = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocat,defaultZoomLevel));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String d1 = "";
                for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                    d1 = restaurant.getLATITUDE();
                    if (d1.equals("#N/A")) {
                        break;
                    }
                    RestaurantList.add(restaurant);
                }

                populateMarksOnMap(9,3);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
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
    private void populateMarksOnMap(int numberRest, double distanceTravel){
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
                    addMarkerMap(testRestaurant, rest.getNAME(), findDistanceNearByRestaurant(testRestaurant));
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
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }
    //add markers to the map
    private void addMarkerMap(LatLng coordinates, String title, double dist) {
        DecimalFormat df = new DecimalFormat("#.##");
        if(title == null) {
            title = "Current Location: %4.3f Lat %4.3f Long.";
        }
        String msg = String.format(title, coordinates.latitude, coordinates.longitude);

        LatLng latlng = new LatLng(coordinates.latitude, coordinates.longitude);
        mMap.addMarker(new MarkerOptions().position(latlng).title(msg).snippet(df.format(dist) + " km"));
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
                    populateMarksOnMap(numRest,2);
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

        btnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                int numRest;

                etNumRestaurants.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "25")});
                if(!etNumRestaurants.getText().toString().equals("")) {
                    numRest =  Integer.parseInt(etNumRestaurants.getText().toString());
                    populateMarksOnMap(numRest,4);
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
        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                int numRest;

                etNumRestaurants.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "25")});
                if(!etNumRestaurants.getText().toString().equals("")) {
                    numRest =  Integer.parseInt(etNumRestaurants.getText().toString());
                    populateMarksOnMap(numRest,6);
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













