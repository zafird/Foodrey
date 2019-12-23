package ca.bcit.comp3717project;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<InspectionRecords> inspectionRecordList = new ArrayList<>();
    private final String TAG = "COMP3717Main_History";
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MyTask().execute();
        setContentView(R.layout.activity_history);
        lv = findViewById(R.id.inspRecordList);
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler webHandler = new HttpHandler();
            String jsonStr = null;
            String urlAPICALL = "http://data.surrey.ca/api/action/datastore_search?resource_id=30b38b66-649f-4507-a632-d5f6f5fe87f1&q=SWOD-APSP3X";

            jsonStr = webHandler.makeServiceCall(urlAPICALL);

            if (jsonStr != null) {

                try {
                    JSONObject jsonArry = new JSONObject(jsonStr);
                    JSONObject jArray = jsonArry.getJSONObject("result");
                    JSONArray jArr = jArray.getJSONArray("records");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject c = jArr.getJSONObject(i);
                        String NumCritical = c.getString("NumCritical");
                        String HazardRating = c.getString("HazardRating");
                        String NumNonCritical = c.getString("NumNonCritical");
                        String InspectionDate = c.getString("InspectionDate");
                        String InspType = c.getString("InspType");
                        String ViolLump = c.getString("ViolLump");
                        String TrackingNumber = c.getString("TrackingNumber");
                        String _id = c.getString("_id");


                        // tmp hash map for single restaurant
                        InspectionRecords inpectionRecords = new InspectionRecords();

                        inspectionRecordList.add(inpectionRecords);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed!!!");
                    e.printStackTrace();
                }

            }else{
                Log.e(TAG, "json null ");

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            InspectionRecordsAdapter adapter = new InspectionRecordsAdapter(HistoryActivity.this, inspectionRecordList);

            // Attach the adapter to a ListView
            lv.setAdapter(adapter);
        }

    }
}
