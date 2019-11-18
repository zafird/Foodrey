package ca.bcit.comp3717project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FavouriteActivity  extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public final static String BROADCAST_ACTION = "com.updateFavourite.action";
    private BottomNavigationView mNavBar;
    private SearchView editsearch;
    private ArrayList<Restaurant> restaurantList;
    private SQLiteOpenHelper helper;
    private ListView lv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        mNavBar = findViewById(R.id.menu_navBar);
        restaurantList = MainActivity.restaurantList;
        mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(this, mNavBar));
        helper = new MyFoodreyDbHelper(this);
        lv = (ListView) findViewById(R.id.lvRestaurant);
        restaurantList = new ArrayList<>();
        editsearch = (SearchView) findViewById(R.id.svFavRestaurant);
        editsearch.setOnQueryTextListener(this);
        setTitle("Foodrey");

        //update Activity from the Service
        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("signal");
                GetFavRestaurants();
                ListViewAdapter adapter = new ListViewAdapter(FavouriteActivity.this, restaurantList);
                lv.setAdapter(adapter);
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intFilt);

        try {
            GetFavRestaurants();

            ListViewAdapter adapter = new ListViewAdapter(FavouriteActivity.this, restaurantList);
            lv.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
        //listen to the close button on the search view and populate 100 items on the list
        editsearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
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
                lv.setAdapter(null);
                ListViewAdapter adapter = new ListViewAdapter(FavouriteActivity.this, restSerachList);
                lv.setAdapter(adapter);
                return false;
            }
        });
    }
    //set the menu to true when active
    @Override
    public void onResume() {
        super.onResume();
        if(mNavBar != null){
            mNavBar.getMenu().findItem(R.id.iBook).setChecked(true);

        }
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

    @Override
    public boolean onQueryTextSubmit(String str) {

        List<Restaurant> restSerachList = new ArrayList<>();
        restSerachList = restaurantList.stream()
                .filter(p -> p.getNAME().toLowerCase().startsWith(str.toLowerCase())).collect(Collectors.toList());

        lv.setAdapter(null);
        ListViewAdapter adapter = new ListViewAdapter(FavouriteActivity.this, restSerachList);
        lv.setAdapter(adapter);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
