package ca.bcit.comp3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final String TAG = "COMP3717Main";
    private DatabaseReference dbRef;
    private BottomNavigationView mNavBar;
    private SQLiteOpenHelper helper;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[] RestaurantNameList;
    static ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
    ListView list_rest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbRef = FirebaseDatabase.getInstance().getReference("restaurants");
        list_rest = findViewById(R.id.lvRestaurant);
        mNavBar = findViewById(R.id.menu_navBar);
        mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(this, mNavBar));
        editsearch = (SearchView) findViewById(R.id.svRestaurant);
        editsearch.setOnQueryTextListener(this);

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

        helper = new MyFoodreyDbHelper(this);


    }
    @Override
    protected void onStart() {
        super.onStart();

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

            ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, restaurantList);
            list_rest.setAdapter(adapter);

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

                    ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, restaurantList, true);
                    list_rest.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

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
//        //firebase search version
//        Log.d("Search", "Query string - " + str);
//        Search task = new Search(this);
//        task.execute(str);

        SQLiteDatabase sqliteDb = helper.getReadableDatabase();
        String query = "SELECT * FROM Restaurant WHERE RESTAURANT like '%" + str + "%' ORDER BY InspectionDate DESC";
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

        list_rest.setAdapter(null);
        ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, restaurantList);
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
