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
import com.rramsauer.iotlocatiotrackerapp.ui.models.UwbToaTableListModel;
/* JAVA imports */
import java.util.List;

/**
 * Class UWB TOA Table List Adapter.
 * The UwbToaTableListAdapter class is responsible for populating
 * the data in a RecyclerView for the TOA distance measurements.
 * Table 2
 *
 * @author Ramsauer René
 * @version V1.3
 * @see Context
 * @see android.view
 * @see androidx.annotation
 * @see RecyclerView
 */
public class UwbToaTableListAdapter extends RecyclerView.Adapter<UwbToaTableListAdapter.ViewHolder> {

    private Context context;
    private List<UwbToaTableListModel> uwbToaTableList;

    /**
     * Constructor for the UwbToaTableListAdapter class.
     *
     * @param context             The context of the activity.
     * @param uwbToaMeasuringList The list of TOA distance measurements to display in the RecyclerView.
     * @author Ramsauer René
     */
    public UwbToaTableListAdapter(Context context, List<UwbToaTableListModel> uwbToaMeasuringList) {
        this.context = context;
        this.uwbToaTableList = uwbToaMeasuringList;
    }

    /**
     * Called when a new ViewHolder is created in the RecyclerView.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @author Ramsauer René
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.uwb_toa_table_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called when the data is bound to a ViewHolder in the RecyclerView.
     *
     * @param holder   The ViewHolder to bind the data to.
     * @param position The position of the data in the list.
     * @author Ramsauer René
     */
    @Override
    public void onBindViewHolder(@NonNull UwbToaTableListAdapter.ViewHolder holder, int position) {
        if (uwbToaTableList != null && uwbToaTableList.size() > 0) {
            UwbToaTableListModel model = uwbToaTableList.get(position);
            holder.uwb_toa_tableview_time.setText(model.getTime());
            holder.uwb_toa_tableview_distance_toa.setText(model.getToaDistanceValue());
        } else {
            return;
        }
    }

    /**
     * Returns the number of items in the list to be displayed in the RecyclerView.
     *
     * @return The number of items in the list.
     * @author Ramsauer René
     */
    @Override
    public int getItemCount() {
        return uwbToaTableList.size();
    }

    /**
     * The ViewHolder class that holds references to the views that need to be populated with data in the RecyclerView.
     *
     * @author Ramsauer René
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView uwb_toa_tableview_time, uwb_toa_tableview_distance_rssi, uwb_toa_tableview_distance_toa;

        /**
         * Constructor for the ViewHolder class.
         *
         * @param itemView The View that this ViewHolder will hold references to.
         * @author Ramsauer René
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uwb_toa_tableview_time = itemView.findViewById(R.id.uwb_toa_tableview_time);
            uwb_toa_tableview_distance_toa = itemView.findViewById(R.id.uwb_toa_tableview_distance_toa);
        }
    }
}
