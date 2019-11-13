package ca.bcit.comp3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private final String TAG = "COMP3717Main";

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
        mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(this, mNavBar));
        editsearch = (SearchView) findViewById(R.id.svRestaurant);
        editsearch.setOnQueryTextListener(this);

//        Search task = new Search(this);
//        task.execute("");

        ListView list_rest = findViewById(R.id.lvRestaurant);
        list_rest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Restaurant res = (Restaurant) adapterView.getItemAtPosition((int)l);
                Intent intent = new Intent(MainActivity.this, Detail.class);
                intent.putExtra("index", (int) l);
                intent.putExtra("name",res.getNAME());
                intent.putExtra("address",res.getPHYSICALADDRESS());
                intent.putExtra("city",res.getPHYSICALCITY());
                intent.putExtra("rating",res.getHazardRating());
                intent.putExtra("date",res.getInspectionDate());
                intent.putExtra("critical",res.getNumCritical());
                intent.putExtra("noncritical",res.getNumNonCritical());
                startActivity(intent);
            }
        });

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
