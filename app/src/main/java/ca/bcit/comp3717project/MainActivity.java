package ca.bcit.comp3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView mNavBar = findViewById(R.id.menu_navBar);
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
    }

    private void actionMenuItems() {

    }


    private void goToMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void search(View view) {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

}
