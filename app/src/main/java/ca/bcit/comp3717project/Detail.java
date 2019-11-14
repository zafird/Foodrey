package ca.bcit.comp3717project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Detail extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap googleMap;
    private double latitude;
    private double longitude;
    private float defaultZoomLevel = 10.0f;
    private SQLiteOpenHelper helper = new MyFoodreyDbHelper(this);
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ArrayAdapter<Restaurant> arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1);

        restaurantName = (String) Objects.requireNonNull(getIntent().getExtras()).getString("name");
        restaurantAddress = (String) Objects.requireNonNull(getIntent().getExtras()).getString("address");
        restaurantCity = (String) Objects.requireNonNull(getIntent().getExtras()).getString("city");
        String restaurantHazard = "Hazard Rating: "
                + (String) Objects.requireNonNull(getIntent().getExtras()).getString("rating");
        String restaurantDate = "Date Inspected: " +
                (String) Objects.requireNonNull(getIntent().getExtras()).getString("date");
        String restaurantCritical = "Critical Hazards: "
                + Objects.requireNonNull(getIntent().getExtras()).getInt("critical");
        String restaurantNonCritical = "Non-Critical Hazards: "
                + (Objects.requireNonNull(getIntent().getExtras()).getInt("noncritical"));
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
        if((Objects.requireNonNull(getIntent().getExtras()).getString("rating")).equals("Low")){
            rating.setTextColor(getResources().getColor(R.color.green));
        } else if((Objects.requireNonNull(getIntent().getExtras()).getString("rating")).equals("Moderate")){
            rating.setTextColor(getResources().getColor(R.color.orange));
        } else if((Objects.requireNonNull(getIntent().getExtras()).getString("rating")).equals("High")){
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
            SQLiteDatabase sqliteDb = helper.getWritableDatabase();
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
                .position(new LatLng(latitude,longitude)).title("Marker"));
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
        SQLiteDatabase sqliteDb = helper.getWritableDatabase();
        try {
            if(fav.isChecked()) {
                ContentValues values = new ContentValues();
                values.put("RESTAURANT", restaurantName);
                values.put("CITY", restaurantCity);
                values.put("ADDRESS", restaurantAddress);
                values.put("CREATED_AT", Calendar.getInstance().getTimeInMillis());

                sqliteDb.insert("Favorite", null, values);
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
}
