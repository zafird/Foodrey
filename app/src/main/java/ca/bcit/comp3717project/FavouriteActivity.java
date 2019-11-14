package ca.bcit.comp3717project;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FavouriteActivity  extends AppCompatActivity {
    private BottomNavigationView mNavBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        mNavBar = findViewById(R.id.menu_navBar);
        mNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewListener(this, mNavBar));

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
