package com.rramsauer.iotlocatiotrackerapp.ui.adapter;

/* Android imports */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/* Androidx imports */
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
/* Application imports */
import com.rramsauer.iotlocatiotrackerapp.R;
import com.rramsauer.iotlocatiotrackerapp.ui.models.UwbRssiTableListModel;
/* JAVA imports */
import java.util.List;


/**
 * Class UWB RSSI Table List Adapter.
 * UwbRssiTableListAdapter is a RecyclerView adapter that binds UwbRssiTableListModel data to
 * the view holder to display a table of UWB RSSI measurements.
 * Table 1
 *
 * @author Ramsauer René
 * @version V1.3
 * @see Context
 * @see android.view
 * @see androidx.annotation
 * @see RecyclerView
 */
public class UwbRssiTableListAdapter extends RecyclerView.Adapter<UwbRssiTableListAdapter.ViewHolder> {

    private Context context;
    private List<UwbRssiTableListModel> uwbRssiTableList;

    /**
     * Constructor for the UwbRssiTableListAdapter class.
     *
     * @param context              the context of the activity or fragment where the adapter is being used.
     * @param uwbRssiMeasuringList the list of UwbRssiTableListModel objects to be displayed.
     * @author Ramsauer René
     */
    public UwbRssiTableListAdapter(Context context, List<UwbRssiTableListModel> uwbRssiMeasuringList) {
        this.context = context;
        this.uwbRssiTableList = uwbRssiMeasuringList;
    }

    /**
     * Called when a new ViewHolder is needed in the RecyclerView.
     *
     * @param parent   the ViewGroup into which the new View will be added.
     * @param viewType the type of the new View.
     * @return a new ViewHolder that holds a View of the given view type.
     * @author Ramsauer René
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.uwb_rssi_table_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called when a ViewHolder is bound to data in the RecyclerView.
     *
     * @param holder   the ViewHolder to be bound to data.
     * @param position the position of the data in the list.
     * @author Ramsauer René
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (uwbRssiTableList != null && uwbRssiTableList.size() > 0) {
            UwbRssiTableListModel model = uwbRssiTableList.get(position);
            holder.uwb_rssi_tableview_time.setText(model.getTime());
            holder.uwb_rssi_tableview_rssi.setText(model.getRssiValue());
            holder.uwb_rssi_tableview_filtered_rssi.setText(model.getFilterRssiValue());
            holder.uwb_rssi_tableview_distance_rssi.setText(model.getDistanceRssiValue());
        } else {
            return;
        }
    }

    /**
     * Returns the number of items in the data set.
     *
     * @return the number of items in the data set.
     * @author Ramsauer René
     */
    @Override
    public int getItemCount() {
        return uwbRssiTableList.size();
    }

    /**
     * ViewHolder class that holds the views for the UWB RSSI table row.
     *
     * @author Ramsauer René
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView uwb_rssi_tableview_time, uwb_rssi_tableview_rssi, uwb_rssi_tableview_filtered_rssi, uwb_rssi_tableview_distance_rssi;

        /**
         * Constructor for the ViewHolder class.
         *
         * @param itemView the View object that represents the UWB RSSI table row.
         * @author Ramsauer René
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uwb_rssi_tableview_time = itemView.findViewById(R.id.uwb_rssi_tableview_time);
            uwb_rssi_tableview_rssi = itemView.findViewById(R.id.uwb_rssi_tableview_rssi);
            uwb_rssi_tableview_filtered_rssi = itemView.findViewById(R.id.uwb_rssi_tableview_rssi_filtered);
            uwb_rssi_tableview_distance_rssi = itemView.findViewById(R.id.uwb_rssi_tableview_distance);
        }
    }
}
