package ca.bcit.comp3717project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private final String TAG = "COMP3717Main1";
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[] RestaurantNameList;
    ArrayList<Restaurant> arraylist = new ArrayList<Restaurant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView mNavBar = findViewById(R.id.menu_navBar);
        list = (ListView) findViewById(R.id.lvRestaurant);
        mNavBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.iHome:
                                System.out.println("Menu Item Home");
                                break;
                            case R.id.iMap:
                                System.out.println("Menu Item Map");
                                goToMapActivity();
                                break;
                            case R.id.iBook:
                                System.out.println("Menu Item Book");
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

        // Locate the EditText in listview_main.xml
        editsearch = (SearchView) findViewById(R.id.svRestaurant);
        editsearch.setOnQueryTextListener(this);
    }

    private void actionMenuItems() {

    }


    private void goToMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Log.d("Search", "Query string - " + query);
        Search task = new Search(this);
        task.execute(query);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
