package ca.bcit.comp3717project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class BottomNavigationViewListener implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Context originalContext;
    private BottomNavigationView bottomNavigationView;

    public BottomNavigationViewListener(Context context, BottomNavigationView bnView){
        this.originalContext = context;
        this.bottomNavigationView = bnView;
        checkCurrentMenuItem();
    }

    private void checkCurrentMenuItem() {
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem;
        if(originalContext.getClass().getSimpleName().equals("MainActivity")){
            menuItem = menu.getItem(0);
            menuItem.setChecked(true);
        }else if(originalContext.getClass().getSimpleName().equals("FavouriteActivity")){
            menuItem = menu.getItem(2);
            menuItem.setChecked(true);
        }else if(originalContext.getClass().getSimpleName().equals("MapsActivity")){
            menuItem = menu.getItem(1);
            menuItem.setChecked(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
            switch (menuItem.getItemId()) {
                case R.id.iHome:
                    if(!originalContext.getClass().getSimpleName().equals("MainActivity"))
                        goToMainActivity();
                    break;
                case R.id.iMap:
                    goToMapActivity();
                    break;
                case R.id.iBook:
                    if(!originalContext.getClass().getSimpleName().equals("FavouriteActivity"))
                        goToFavouriteActivity();
                    break;
                default:
                    break;
            }
            return true;
    }
    private void goToMainActivity() {
        Intent intent = new Intent(originalContext, MainActivity.class);
        intent.putExtra("nav",  1);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        originalContext.startActivity(intent);
    }

    private void goToMapActivity() {
        Intent intent = new Intent(originalContext, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        originalContext.startActivity(intent);
    }

    private void goToFavouriteActivity() {
        Intent intent = new Intent(originalContext, FavouriteActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        originalContext.startActivity(intent);
    }
}
