package ca.bcit.comp3717project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap googleMap;
    private Double latitude;
    private Double longitude;
    private float defaultZoomLevel = 10.0f;
    private SQLiteOpenHelper helper = new MyFoodreyDbHelper(this);
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantCity;
    private String restaurantHazardValue;
    private String restaurantDateValue;
    private int restaurantCriticalValue;
    private int restaurantNonCriticalValue;
    private SQLiteDatabase sqliteDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ArrayAdapter<Restaurant> arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1);

        restaurantName = (String) Objects.requireNonNull(getIntent().getExtras()).getString("name");
        restaurantAddress = (String) Objects.requireNonNull(getIntent().getExtras()).getString("address");
        restaurantCity = (String) Objects.requireNonNull(getIntent().getExtras()).getString("city");
        restaurantHazardValue = (String) Objects.requireNonNull(getIntent().getExtras()).getString("rating");
        String restaurantHazard = "Hazard Rating: " + restaurantHazardValue;
        restaurantDateValue = (String) Objects.requireNonNull(getIntent().getExtras()).getString("date");
        String restaurantDate = "Date Inspected: " + restaurantDateValue;
        restaurantCriticalValue = (Integer) Objects.requireNonNull(getIntent().getExtras()).getInt("critical");
        String restaurantCritical = "Critical Hazards: " + restaurantCriticalValue;
        restaurantNonCriticalValue = (Integer) (Objects.requireNonNull(getIntent().getExtras()).getInt("noncritical"));
        String restaurantNonCritical = "Non-Critical Hazards: " + restaurantNonCriticalValue;
        TextView name = findViewById(R.id.name);
        TextView address = findViewById(R.id.address);
        TextView city = findViewById(R.id.city);
        SQLiteOpenHelper helper = new MyFoodreyDbHelper(this);
        TextView rating = findViewById(R.id.rating);
        TextView date = findViewById(R.id.date);
        TextView critical = findViewById(R.id.critical);
        TextView noncritical = findViewById(R.id.noncritical);
        CheckBox favCheckbox = findViewById(R.id.favorite);

        name.setText(restaurantName);
        address.setText(restaurantAddress);
        city.setText(restaurantCity);
        rating.setText(restaurantHazard);
        if("Low".equals(Objects.requireNonNull(getIntent().getExtras()).getString("rating"))){
            rating.setTextColor(getResources().getColor(R.color.green));
        } else if("Moderate".equals(Objects.requireNonNull(getIntent().getExtras()).getString("rating"))){
            rating.setTextColor(getResources().getColor(R.color.orange));
        } else if("High".equals(Objects.requireNonNull(getIntent().getExtras()).getString("rating"))){
            rating.setTextColor(getResources().getColor(R.color.red));
        }
        date.setText(restaurantDate);
        critical.setText(restaurantCritical);
        noncritical.setText(restaurantNonCritical);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitude = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(getIntent()
                .getExtras()).getString("latitude")));

        longitude = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(getIntent()
                .getExtras()).getString("longitude")));

        try {
            sqliteDb = helper.getWritableDatabase();
            Cursor cursor = sqliteDb.query("Favorite",
                    new String[] {"Restaurant"},
                    "Restaurant = ?",
                    new String[] {restaurantName},
                    null, null, null);

            // move to the first record
            if (cursor.moveToFirst()) {
                // get the country details from the cursor
                favCheckbox.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMapReady(GoogleMap map) {
        LatLng object = new LatLng(
                latitude, longitude);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude)).title(restaurantName));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(object, defaultZoomLevel));
    }
    public void setUpMap(){

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void onFavoriteClicked(View view) {
        // set the Favorite CheckBox value
        CheckBox fav = findViewById(R.id.favorite);
        try {
            if(fav.isChecked()) {
                Restaurant r = new Restaurant();
                r.setNAME(restaurantName);
                r.setPHYSICALCITY(restaurantCity);
                r.setPHYSICALADDRESS(restaurantAddress);
                r.setHazardRating(restaurantHazardValue);
                r.setInspectionDate(restaurantDateValue);
                r.setNumCritical(restaurantCriticalValue);
                r.setNumNonCritical(restaurantNonCriticalValue);
                r.setLATITUDE(String.valueOf(latitude));
                r.setLONGITUDE(String.valueOf(longitude));
                ((MyFoodreyDbHelper)helper).insertFavorite(sqliteDb, r);
            } else {
                sqliteDb.execSQL("DELETE FROM Favorite WHERE RESTAURANT = '"+restaurantName+"';");
            }
        } catch (SQLiteException sqlex) {
            String msg = "[DB unavailable]";
            msg += "\n\n" + sqlex.toString();

            Toast t = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            t.show();
        }
    }
//    @Override
//    public void onBackPressed() {
//        // Do Here what ever you want do on back press;
//        super.onBackPressed();
//        finish();
//    }
}
