
package ca.bcit.comp3717project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<Restaurant> restaurantList = null;
    private ArrayList<Restaurant> arraylist;
    private final int[] colors = new int [] {R.color.lightpink, R.color.primaryTextColor};

    public ListViewAdapter(Context context, List<Restaurant> restaurantList) {
        mContext = context;
        this.restaurantList = restaurantList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Restaurant>();
        this.arraylist.addAll(restaurantList);
    }

    public ListViewAdapter(Context context, List<Restaurant> restaurantList, boolean dataInit) {
        this(context, restaurantList);

//        if(dataInit) {
//            SQLiteOpenHelper helper = new MyFoodreyDbHelper(mContext);
//            SQLiteDatabase sqliteDb = helper.getWritableDatabase();
//            ((MyFoodreyDbHelper)helper).getSyncFirebaseDB(sqliteDb, (ArrayList<Restaurant>) restaurantList);
//        }
    }

    public class ViewHolder {
        TextView name;
        TextView address;
        TextView city;
        TextView rating;
        TextView inspectionDate;
    }

    @Override
    public int getCount() {
        return restaurantList.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return restaurantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_layout, null);
            // Locate the TextViews in listview_item.xml
            if(position%2 == 0){
                view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightpink));
            }
            holder.name = (TextView) view.findViewById(R.id.restaurantName);
            holder.address= (TextView) view.findViewById(R.id.textAddress);
//            holder.city= (TextView) view.findViewById(R.id.textCity);
//            holder.rating= (TextView) view.findViewById(R.id.textRating);
//            holder.inspectionDate= (TextView) view.findViewById(R.id.textInspectionDate);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
            int colorPosition = position % colors.length;
            view.setBackgroundResource(colors[colorPosition]);
        }

        // Set the results into TextViews
        holder.name.setText(restaurantList.get(position).getNAME());
        holder.address.setText(restaurantList.get(position).getPHYSICALADDRESS() +
                " " + restaurantList.get(position).getPHYSICALCITY());
//        holder.city.setText(restaurantList.get(position).getPHYSICALCITY());

//        holder.rating.setText(String.format("Hazard Rating: %s", restaurantList.get(position).getHazardRating()));
//        if("Low".equals(restaurantList.get(position).getHazardRating())){
//            holder.rating.setTextColor(Color.GREEN);
//        } else if("Moderate".equals(restaurantList.get(position).getHazardRating())){
//            holder.rating.setTextColor(ContextCompat.getColor(mContext, R.color.orange));
//        } else if("High".equals(restaurantList.get(position).getHazardRating())){
//            holder.rating.setTextColor(Color.RED);
//        }

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        try {
//            String dateStr = restaurantList.get(position).getInspectionDate();
//            if(dateStr.equals("none")){
//                holder.inspectionDate.setText(dateStr);
//            }else{
//                Date d1 = sdf.parse(dateStr);
//                sdf.applyPattern("MM/dd/yyyy");
//                holder.inspectionDate.setText(String.format("InspectionDate: %s", sdf.format(d1)));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        restaurantList.clear();
        if (charText.length() == 0) {
            restaurantList.addAll(arraylist);
        } else {
            for (Restaurant r : arraylist) {
                if (r.getNAME().toLowerCase(Locale.getDefault()).contains(charText)) {
                    restaurantList.add(r);
                }
            }
        }
        notifyDataSetChanged();
    }

}