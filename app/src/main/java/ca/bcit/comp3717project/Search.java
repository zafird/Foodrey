package ca.bcit.comp3717project;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Search extends AsyncTask<String, Void, ArrayList<Restaurant>> {

    private final String TAG = "COMP3717Search";
    private FirebaseDatabase database;
    DatabaseReference myRef;
    private Context mContext;
    private ArrayList<Restaurant> arraylist = new ArrayList<Restaurant>();
    private WeakReference<MainActivity> activictyRef;

    public Search (Context context){
        mContext = context;
        activictyRef = new WeakReference<MainActivity>((MainActivity) context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // connect the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("restaurants");
    }

    @Override
    protected ArrayList<Restaurant> doInBackground(String... searchStr) {
        String restaurantName = searchStr[0];
        // DB query : Generate relevant data
        myRef.orderByChild("NAME")
//                .equalTo(restaurantName)
                .startAt(restaurantName)
                .endAt(restaurantName + "\uf8ff")
                .limitToFirst(20)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String prevRestaurant = "";
                        for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                            Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                            if (!prevRestaurant.equals(restaurant.getNAME()))
                                arraylist.add(restaurant);
                            prevRestaurant = restaurant.getNAME();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Database unavailable", error.toException());
                        Toast t = Toast.makeText(mContext,
                                "Database unavailable", Toast.LENGTH_SHORT);
                        t.show();
                    }
                });

        return arraylist;
    }

    @Override
    protected void onPostExecute(ArrayList<Restaurant> arraylist) {
        super.onPostExecute(arraylist);
        ListViewAdapter adapter = new ListViewAdapter(mContext, arraylist);
        MainActivity a = activictyRef.get();
        a.list.setAdapter(adapter);
//        ListView list = (ListView) findViewById(R.id.lvRestaurant);
//        list.setAdapter(adapter);
    }
}
