package ca.bcit.comp3717project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottom_navigation, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iHome:
                // User chose the "Settings" item, show the app settings UI...
                System.out.println("Menu Item Home");
                return true;
            case R.id.iMap:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                System.out.println("Menu Item Map");
                goToMapActivity();
                return true;
            case R.id.iBook:
                System.out.println("Menu Item Book");
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void goToMapActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void search(View view){
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

}
