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
import com.rramsauer.iotlocatiotrackerapp.ui.models.BLETableListModel;
/* JAVA imports */
import java.util.List;

/**
 * Class BLE Table List Adapter.
 * Adapter class for the RecyclerView to display BLE measuring data in a table format.
 *
 * @author Ramsauer René
 * @version V1.3
 * @see android.content.Context
 * @see android.view
 * @see androidx.annotation
 * @see androidx.recyclerview.widget.RecyclerView
 */
public class BLETableListAdapter extends RecyclerView.Adapter<BLETableListAdapter.ViewHolder> {

    private Context context;
    private List<BLETableListModel> bleTableList;

    /**
     * Constructs an instance of the BLETableListAdapter.
     *
     * @param context          the context of the adapter.
     * @param bleMeasuringList the list of BLETableListModel objects to display in the adapter.
     * @author Ramsauer René
     */
    public BLETableListAdapter(Context context, List<BLETableListModel> bleMeasuringList) {
        this.context = context;
        this.bleTableList = bleMeasuringList;
    }

    /**
     * Creates a new ViewHolder object and inflates the layout for the BLE table.
     *
     * @param parent   the parent ViewGroup.
     * @param viewType the view type.
     * @return a new ViewHolder object.
     * @author Ramsauer René
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ble_table_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Populates the ViewHolder with data from the BLETableListModel object at the specified position.
     *
     * @param holder   the ViewHolder object to populate.
     * @param position the position of the BLETableListModel object in the list.
     * @author Ramsauer René
     */
    @Override
    public void onBindViewHolder(@NonNull BLETableListAdapter.ViewHolder holder, int position) {
        if (bleTableList != null && bleTableList.size() > 0) {
            BLETableListModel model = bleTableList.get(position);
            holder.ble_tableview_time.setText(model.getTime());
            holder.ble_tableview_rssi.setText(model.getRssiValue());
            holder.ble_tableview_filtered_rssi.setText(model.getFilterRssiValue());
            holder.ble_tableview_distance.setText(model.getDistanceValue());
        } else {
            return;
        }
    }

    /**
     * Returns the number of items in the BLETableListAdapter.
     *
     * @return the number of items in the BLETableListAdapter.
     * @author Ramsauer René
     */
    @Override
    public int getItemCount() {
        return bleTableList.size();
    }

    /**
     * ViewHolder class for the BLE table view.
     *
     * @author Ramsauer René
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ble_tableview_time, ble_tableview_rssi, ble_tableview_filtered_rssi, ble_tableview_distance;

        /**
         * Constructs a new ViewHolder object for the BLE table view.
         *
         * @param itemView the View object for the BLE table view.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ble_tableview_time = itemView.findViewById(R.id.ble_tableview_time);
            ble_tableview_rssi = itemView.findViewById(R.id.ble_tableview_rssi);
            ble_tableview_filtered_rssi = itemView.findViewById(R.id.ble_tableview_filtered_rssi);
            ble_tableview_distance = itemView.findViewById(R.id.ble_tableview_distance);
        }
    }
}
