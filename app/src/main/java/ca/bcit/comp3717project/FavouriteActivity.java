package ca.bcit.comp3717project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;

public class FavouriteActivity  extends AppCompatActivity {
    private BottomNavigationView mNavBar;
    private SQLiteOpenHelper helper = new MyFoodreyDbHelper(this);
    private ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        mNavBar = findViewById(R.id.menu_navBar);
        restaurantList = (ArrayList<Restaurant>) getIntent().getSerializableExtra("listRest");
        mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(this, mNavBar));
        SQLiteOpenHelper db = new MyFoodreyDbHelper(this);

        try {
            ArrayList<HashMap<String, String>> restaurantList = GetFavRestaurants();
            ListView lv = (ListView) findViewById(R.id.lvRestaurant);
            ListAdapter adapter = new SimpleAdapter(FavouriteActivity.this, restaurantList, R.layout.list_row,new String[]{"name","city","address"}, new int[]{R.id.name, R.id.city, R.id.address});
            lv.setAdapter(adapter);
//            Button back = (Button)findViewById(R.id.btnBack);
//            back.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intent = new Intent(DetailsActivity.this,MainActivity.class);
//                    startActivity(intent);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Get Favorite Restaurants
    public ArrayList<HashMap<String, String>> GetFavRestaurants(){
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<HashMap<String, String>> restaurantList = new ArrayList<>();
        String query = "SELECT _ID, RESTAURANT, CITY, ADDRESS FROM Favorite";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> r = new HashMap<>();
            r.put("name",cursor.getString(cursor.getColumnIndex("RESTAURANT")));
            r.put("city",cursor.getString(cursor.getColumnIndex("CITY")));
            r.put("address",cursor.getString(cursor.getColumnIndex("ADDRESS")));
            restaurantList.add(r);
        }
        return  restaurantList;
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
    protected void onRestart() {
        super.onRestart();
        Menu menu = mNavBar.getMenu();
        MenuItem menuItem = menu.getItem(2);

        if(this.getClass().getSimpleName().equals("FavouriteActivity")){
            menuItem.setChecked(true);
        }
    }
}
