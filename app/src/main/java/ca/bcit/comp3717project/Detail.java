package ca.bcit.comp3717project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ArrayAdapter<Restaurant> arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1);

        String restaurantName = (String) Objects.requireNonNull(getIntent().getExtras()).getString("name");
        String restaurantAddress = (String) Objects.requireNonNull(getIntent().getExtras()).getString("address");
        String restaurantCity = (String) Objects.requireNonNull(getIntent().getExtras()).getString("city");
        String restaurantHazard = "Hazard Rating: "
                + (String) Objects.requireNonNull(getIntent().getExtras()).getString("rating");
        String restaurantDate = "Date Inspected: " +
                (String) Objects.requireNonNull(getIntent().getExtras()).getString("date");
        String restaurantCritical = "Number of Critical Hazards: "
                + Objects.requireNonNull(getIntent().getExtras()).getInt("critical");
        String restaurantNonCritical = "Number of Non-Critical Hazards: "
                + (Objects.requireNonNull(getIntent().getExtras()).getInt("noncritical"));
        TextView name = findViewById(R.id.name);
        TextView address = findViewById(R.id.address);
        TextView city = findViewById(R.id.city);
        TextView rating = findViewById(R.id.rating);
        TextView date = findViewById(R.id.date);
        TextView critical = findViewById(R.id.critical);
        TextView noncritical = findViewById(R.id.noncritical);

        name.setText(restaurantName);
        address.setText(restaurantAddress);
        city.setText(restaurantCity);
        rating.setText(restaurantHazard);
        date.setText(restaurantDate);
        critical.setText(restaurantCritical);
        noncritical.setText(restaurantNonCritical);
    }
}
