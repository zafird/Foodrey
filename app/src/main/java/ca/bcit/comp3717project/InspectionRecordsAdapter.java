package ca.bcit.comp3717project;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class InspectionRecordsAdapter  extends ArrayAdapter<InspectionRecords> {
    Context _context;
    ArrayList<InspectionRecords> inspRecords;
    public InspectionRecordsAdapter(Context context, ArrayList<InspectionRecords> inspRecords) {
        super(context, 0, inspRecords);
        inspRecords = inspRecords;
        _context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;
        // Get the data item for this position
        InspectionRecords record = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_history_layout, parent, false);
        }
        // Lookup view for data population
        TextView tvInspDate = convertView.findViewById(R.id.tvInspDate);
        TextView tvInspType = convertView.findViewById(R.id.tvInspType);
        TextView tvHazardRating = convertView.findViewById(R.id.tvHazardRating);
        TextView tvNumCritical = convertView.findViewById(R.id.tvNumCritical);
        TextView tvNumNonCritical = convertView.findViewById(R.id.tvNumNonCritical);
        TextView ViolLump = convertView.findViewById(R.id.ViolLump);
        // Populate the data into the template view using the data object
        tvInspDate.setText(record.getInspectionDate());
        tvInspType.setText(record.getInspType());
        tvHazardRating.setText(record.getHazardRating());
        tvNumCritical.setText(record.getNumCritical());
        tvNumNonCritical.setText(record.getNumNonCritical());
        ViolLump.setText(record.getViolLump());


        // Return the completed view to render on screen
        return convertView;
    }
}
