package ca.bcit.comp3717project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class FavouriteActivity  extends AppCompatActivity {
    private BottomNavigationView mNavBar;
    private SearchView editsearch;
    private ArrayList<Restaurant> restaurantList;
    private SQLiteOpenHelper helper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        mNavBar = findViewById(R.id.menu_navBar);
        mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(this, mNavBar));
        helper = new MyFoodreyDbHelper(this);
        ListView lv = (ListView) findViewById(R.id.lvRestaurant);
        restaurantList = new ArrayList<>();

        try {
            GetFavRestaurants();

            ListViewAdapter adapter = new ListViewAdapter(FavouriteActivity.this, restaurantList);
            //ListAdapter adapter = new SimpleAdapter(FavouriteActivity.this, restaurantList, R.layout.list_row,new String[]{"name","city","address"}, new int[]{R.id.name, R.id.city, R.id.address});
            lv.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        editsearch = (SearchView) findViewById(R.id.svFavRestaurant);
//        editsearch.setOnSQLiteQueryTextListener(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Restaurant res = (Restaurant) adapterView.getItemAtPosition((int)l);
                Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
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
    }


    // Get Favorite Restaurants
    public void GetFavRestaurants(){
        SQLiteDatabase db = helper.getWritableDatabase();
        String query = "SELECT * FROM Favorite";
        Cursor cursor = db.rawQuery(query,null);
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
    }

    private void checkCurrentMenuItem() {
        Menu menu = mNavBar.getMenu();
        MenuItem menuItem;
        if(this.getClass().getSimpleName().equals("MainActivity")){
            menuItem = menu.getItem(0);
            menuItem.setChecked(true);
        }else if(this.getClass().getSimpleName().equals("MainActivity")){
            menuItem = menu.getItem(2);
            menuItem.setChecked(true);
        }
    }
}
