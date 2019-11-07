package ca.bcit.comp3717project;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
            switch (menuItem.getItemId()) {
                case R.id.iHome:
                    System.out.println("Menu Item Home");
                    System.out.println("Woor"+originalContext.getClass().getSimpleName());
                    if(!originalContext.getClass().getSimpleName().equals("MainActivity"))
                        goToMainActivity();
                    break;
                case R.id.iMap:
                    System.out.println("Menu Item Map");
                    goToMapActivity();
                    break;
                case R.id.iBook:
                    System.out.println("Menu Item Book");
                    System.out.println("Woor"+originalContext.getClass().getSimpleName());
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
        originalContext.startActivity(intent);
    }

    private void goToMapActivity() {
        Intent intent = new Intent(originalContext, MapsActivity.class);
        originalContext.startActivity(intent);
    }

    private void goToFavouriteActivity() {
        Intent intent = new Intent(originalContext, FavouriteActivity.class);
        originalContext.startActivity(intent);
    }
}
