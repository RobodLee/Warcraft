package com.playman.warcraft;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    /**
     * //蓝牙适配器
     */
    private BluetoothAdapter adapter;

    private List<BluetoothDevice> deviceList = new ArrayList<>();

    private RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    /**
     * 广播接收器，用于监听搜索到设备
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //  Log.d(TAG, device.getName());
                deviceList.add(device);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.bluetooth_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerViewAdapter = new RecyclerViewAdapter(deviceList, getApplicationContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        adapter = BluetoothAdapter.getDefaultAdapter();
        //如果支持蓝牙而且没有打开蓝牙功能就打开蓝牙
        if (adapter != null) {
            if (!adapter.isEnabled()) {
                turnOnBluetooth();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //打开可见性
            case R.id.enable_visibility:
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                break;
            //查找设备
            case R.id.find_device:
                deviceList.clear();
                recyclerViewAdapter.notifyDataSetChanged();
                Toast.makeText(this, "查找设备", Toast.LENGTH_SHORT).show();
                if (adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }
                adapter.startDiscovery();
                break;
            //查看已绑定设备
            case R.id.already_bounded:
                deviceList.clear();
                if (adapter.isDiscovering()) {
                    adapter.cancelDiscovery();
                }
                Set<BluetoothDevice> set = adapter.getBondedDevices();
                for (BluetoothDevice device : set) {
                    Log.d(TAG, device.getName());
                    deviceList.add(device);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this , SettingActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册监听器
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
            default:
                break;
        }
    }

    /**
     * 打开蓝牙
     */
    private void turnOnBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(intent);
    }
}