package com.george.breakingblue.bluetooth.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.george.breakingblue.fragment.ConnectFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Bluetooth起動許可申請と検出可能にする申請。
 * Bluetoothデバイスの検出を行うクラス
 */
public class DeviceDiscovery extends BroadcastReceiver {

    public static final int REQUEST_ENABLE = 1;

    private ConnectFragment connectFragment;

    private BluetoothAdapter adapter;

    public DeviceDiscovery(ConnectFragment connectFragment){
        this.connectFragment = connectFragment;
        adapter = BluetoothAdapter.getDefaultAdapter();

        if(adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            connectFragment.startActivity(discoverableIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(BluetoothDevice.ACTION_FOUND.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                System.out.println(device.getName());
                connectFragment.addDevice(device);
            }
        }

        if(BluetoothDevice.ACTION_NAME_CHANGED.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                System.out.println(device.getName());
                connectFragment.addDevice(device);
            }
        }
    }

    public List<BluetoothDevice> getPairedDevices(){
        List<BluetoothDevice> devices = new ArrayList<>();
        devices.addAll(adapter.getBondedDevices());
        return devices;
    }

    public void scanStart(){
        if(adapter.isDiscovering()){
            adapter.cancelDiscovery();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        connectFragment.getActivity().registerReceiver(this, filter);

        adapter.startDiscovery();
    }

    public void scanStop(){
        if(adapter.isDiscovering()){
            adapter.cancelDiscovery();
        }
        connectFragment.getActivity().unregisterReceiver(this);
    }

    public boolean isDiscovering(){
        return adapter.isDiscovering();
    }
}
