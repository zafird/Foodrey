package ca.bcit.comp3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final String TAG = "COMP3717Main";
    private DatabaseReference dbRef;
    private BottomNavigationView mNavBar;
    private SQLiteOpenHelper helper;
    private LocationManager locationManager;
    ListViewAdapter adapter;
    SearchView editsearch;
    static ArrayList<Restaurant> restaurantList = new ArrayList<>();
    ListView list_rest;
    private Bundle extras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pressedNavBar = 0;
        extras= getIntent().getExtras();
        if(extras != null){
            pressedNavBar = extras.getInt("nav");
        }
        if(pressedNavBar == 1){
            setContentView(R.layout.activity_main);
        }else{
            getSupportActionBar().hide();
            setContentView(R.layout.activity_loading);

        }
        dbRef = FirebaseDatabase.getInstance().getReference("restaurants");
        new MyTask().execute();


    }
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

    }
    private class MyTask extends AsyncTask<Uri, Integer, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(Uri... params) {
            helper = new MyFoodreyDbHelper(MainActivity.this);
            SQLiteDatabase sqliteDb = helper.getReadableDatabase();
            Cursor mCount= sqliteDb.rawQuery("SELECT count(*) cnt FROM Restaurant", null);
            mCount.moveToFirst();
            int cnt = mCount.getInt(0);
            mCount.close();

            if(cnt > 0 ) {
                String query = "SELECT * FROM Restaurant";
                Cursor cursor = sqliteDb.rawQuery(query,null);
                restaurantList.clear();
                while (cursor.moveToNext()){
                    Restaurant r = new Restaurant();
                    r.setNAME(cursor.getString(cursor.getColumnIndex("RESTAURANT")));
                    r.setPHYSICALCITY(cursor.getString(cursor.getColumnIndex("CITY")));
                    r.setPHYSICALADDRESS(cursor.getString(cursor.getColumnIndex("ADDRESS")));
                    r.setHazardRating(cursor.getString(cursor.getColumnIndex("HazardRating")));
                    r.setInspectionDate(cursor.getString(cursor.getColumnIndex("InspectionDate")));
                    r.setNumCritical(cursor.getInt(cursor.getColumnIndex("NumCritical")));
                    r.setNumNonCritical(cursor.getInt(cursor.getColumnIndex("NumNonCritical")));
                    r.setLATITUDE(cursor.getString(cursor.getColumnIndex("LATITUDE")));
                    r.setLONGITUDE(cursor.getString(cursor.getColumnIndex("LONGITUDE")));

                    restaurantList.add(r);
                }
            } else {

                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        restaurantList.clear();
                        for (DataSnapshot bloodPressureSnapshot : dataSnapshot.getChildren()) {
                            Restaurant restaurant = bloodPressureSnapshot.getValue(Restaurant.class);
                            if (!restaurant.getNAME().equals("#N/A")) {
                                restaurantList.add(restaurant);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            int mProgressStatus = 0;
            while(mProgressStatus < 100) {
                mProgressStatus += 2;
                android.os.SystemClock.sleep(105);
            }

            return restaurantList;
        }

        @Override
        protected void onPostExecute(List<Restaurant> result) {
            getSupportActionBar().show();
            setContentView(R.layout.activity_main);
            // set layout elements with data that from the result
            list_rest = findViewById(R.id.lvRestaurant);
            setAdapter();
            mNavBar = findViewById(R.id.menu_navBar);
            mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(MainActivity.this, mNavBar));
            editsearch = (SearchView) findViewById(R.id.svRestaurant);
            editsearch.setOnQueryTextListener(MainActivity.this);

            list_rest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Restaurant res = (Restaurant) adapterView.getItemAtPosition((int)l);
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("index", (int) l);
                    intent.putExtra("name",res.getNAME());
                    intent.putExtra("address",res.getPHYSICALADDRESS());
                    intent.putExtra("city",res.getPHYSICALCITY());
                    intent.putExtra("rating",res.getHazardRating());
                    intent.putExtra("date",res.getInspectionDate());
                    intent.putExtra("critical",res.getNumCritical());
                    intent.putExtra("noncritical",res.getNumNonCritical());
                    intent.putExtra("latitude",res.getLATITUDE());
                    intent.putExtra("longitude",res.getLONGITUDE());
                    startActivity(intent);
                }
            });
            //listen to the close button on the search view and populate 100 items on the list
            editsearch.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    setAdapter();
                    return false;
                }
            });

        }
    }
    private void setAdapter(){
        List<Restaurant> restSerachList = new ArrayList<>();
        int count = 0;
        for(Restaurant r : restaurantList){
            if(count < 100){
                restSerachList.add(r);
            }else{
                break;
            }
            count++;
        }
        list_rest.setAdapter(null);
        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, restSerachList);
        list_rest.setAdapter(adapter);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Menu menu = mNavBar.getMenu();
        MenuItem menuItem = menu.getItem(0);

        if(this.getClass().getSimpleName().equals("MainActivity")){
            menuItem.setChecked(true);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String str) {

        List<Restaurant> restSerachList = new ArrayList<>();
        restSerachList = restaurantList.stream()
                .filter(p -> p.getNAME().toLowerCase().startsWith(str.toLowerCase())).collect(Collectors.toList());



        list_rest.setAdapter(null);
        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, restSerachList);
        list_rest.setAdapter(adapter);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }



}
