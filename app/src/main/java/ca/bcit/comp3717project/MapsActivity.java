package ca.bcit.comp3717project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "HumbleMaps";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private float defaultZoomLevel = 10.0f;
    private boolean animationInProgress = false;
    private FirebaseDatabase database;
    private Location lastKnownLocation;
    private List<Restaurant> RestaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Read from the database
        RestaurantList = new ArrayList<Restaurant>();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //TODO
        //Tried : https://stackoverflow.com/questions/37959751/how-to-use-font-awesome-icon-in-android-application
        //Result: Using fontawesome class is not working with view.inflate.Exception
        //Instead fontawesome button style
        Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        Button buttonMinus = (Button)findViewById( R.id.btnZoomOut );
        Button buttonPlus = (Button)findViewById( R.id.btnZoomIn );
        buttonMinus.setTypeface(font);
        buttonPlus.setTypeface(font);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("restaurants");

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
                LatLng testRestaurant;
                // Add a marker in Sydney and move the camera
                int counter = 0;
                for(Restaurant rest : RestaurantList) {
                    if (rest.getLATITUDE() != "#N/A" || rest.getLONGITUDE() != "#N/A" || rest.getNAME() != "#N/A") {
                        testRestaurant = new LatLng(Double.valueOf(rest.getLATITUDE()),
                                Double.valueOf(rest.getLONGITUDE()));
                        System.out.println("Distance" + findDistanceNearByRestaurant(testRestaurant));
                        if (findDistanceNearByRestaurant(testRestaurant) < 2.0) {
                            addMarker2Map(testRestaurant, rest.getNAME());
                            counter++;
                        }
                    }
                    if(counter > 5){
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        //get current location.
        onCurrentLocation();
        // Add a marker in Sydney and move the camera
        LatLng currLocat = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocat,defaultZoomLevel));
    }

    public void onSearch(View v) {
        List<Address> addressList = null;
        EditText editTextLocation = (EditText) findViewById(R.id.editTextLocation);
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

    private void addMarker2MapDraw(Location location, String title) {

        if(title == null) {
            title = "Current Location: %4.3f Lat %4.3f Long.";
        }
        String msg = String.format(title,
                location.getLatitude(),
                location.getLongitude());

        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latlng).title(msg));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,defaultZoomLevel));
    }

    private void addMarker2Map(LatLng coordinates, String title) {

        if(title == null) {
            title = "Current Location: %4.3f Lat %4.3f Long.";
        }
        String msg = String.format(title, coordinates.latitude, coordinates.longitude);

        LatLng latlng = new LatLng(coordinates.latitude, coordinates.longitude);
        mMap.addMarker(new MarkerOptions().position(latlng).title(msg));
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
}
