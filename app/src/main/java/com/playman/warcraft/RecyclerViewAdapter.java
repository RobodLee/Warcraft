package com.playman.warcraft;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<BluetoothDevice> bluetoothDeviceList;
    private Context context;

    private static final String TAG = "MainActivity";

    public RecyclerViewAdapter(List<BluetoothDevice> bluetoothDeviceList , Context context) {
        this.bluetoothDeviceList = bluetoothDeviceList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetoothdevicelist_item, parent, false);
        holder = new ViewHolder(view);
        holder.deviceView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                adapter.cancelDiscovery();
                int position = holder.getAdapterPosition();
                BluetoothDevice device = bluetoothDeviceList.get(position);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    device.createBond();
                }
                Intent intent = new Intent(context , WarcraftController.class);
                intent.putExtra(WarcraftController.BT_ADDRESS, device.getAddress());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice device = bluetoothDeviceList.get(position);
        holder.bluetoothName.setText(device.getName());
        holder.bluetoothAddress.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        View deviceView;
        TextView bluetoothName;
        TextView bluetoothAddress;

        public ViewHolder(View view) {
            super(view);
            deviceView = view;
            bluetoothName = view.findViewById(R.id.bluetooth_name);
            bluetoothAddress = view.findViewById(R.id.bluetooth_address);
        }
    }
}
