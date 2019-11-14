package ca.bcit.comp3717project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<Restaurant> restaurantList = null;
    private ArrayList<Restaurant> arraylist;

    public ListViewAdapter(Context context, List<Restaurant> restaurantList) {
        mContext = context;
        this.restaurantList = restaurantList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Restaurant>();
        this.arraylist.addAll(restaurantList);
    }

    public class ViewHolder {
        TextView name;
        TextView address;
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
            holder.name = (TextView) view.findViewById(R.id.restaurantName);
            holder.address= (TextView) view.findViewById(R.id.textAddress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(restaurantList.get(position).getNAME());
        holder.address.setText(restaurantList.get(position).getPHYSICALADDRESS());

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