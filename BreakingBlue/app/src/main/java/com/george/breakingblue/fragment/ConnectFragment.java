package com.george.breakingblue.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.breakingblue.R;
import com.george.breakingblue.bluetooth.session.ConnectManager;
import com.george.breakingblue.bluetooth.utils.DeviceDiscovery;
import com.george.breakingblue.fragment.adapter.DeviceAdapter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * デバイスに検索,接続するためのフラグメント
 */
public class ConnectFragment extends Fragment {

    private ListView device_list_view;
    private Button search_Button;
    private Button connect_Button;
    private TextView connect_text_view;

    private DeviceDiscovery deviceDiscovery;
    private DeviceAdapter adapter;

    private BluetoothDevice detectDevice;

    private boolean isDiscovering = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        connect_text_view = (TextView)view.findViewById(R.id.connect_device_textView);

        connect_Button = (Button)view.findViewById(R.id.button_connect);
        connect_Button.setOnClickListener(new OnConnectButtonClick());

        search_Button = (Button)view.findViewById(R.id.button_device_search);
        search_Button.setOnClickListener(new OnSearchDeviceButtonClick());

        adapter = new DeviceAdapter(getContext(), R.layout.device_list, new ArrayList<BluetoothDevice>());
        device_list_view = (ListView)view.findViewById(R.id.device_list_view);
        device_list_view.setAdapter(adapter);
        device_list_view.setOnItemClickListener(new OnDeviceListViewItemClick());

        deviceDiscovery = new DeviceDiscovery(this);
    }

    public void addDevice(BluetoothDevice device){
        adapter.add(device);
    }

    private class OnSearchDeviceButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            adapter.clear();

            if(!isDiscovering){
                deviceDiscovery.scanStart();
                adapter.addAll(deviceDiscovery.getPairedDevices());
                search_Button.setText("検索中止");
                isDiscovering = true;
            }else{
                deviceDiscovery.scanStop();
                search_Button.setText("デバイス検索");
                isDiscovering = false;
            }
        }
    }

    private class OnConnectButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(detectDevice != null){
                search_Button.callOnClick();
                new AsyncConnectDevice(detectDevice).execute();
            }else{
                Toast.makeText(getContext(), "接続するデバイスを選択してください", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OnDeviceListViewItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            connect_text_view.setText("デバイス名:" + adapter.getItem(position).getName());
            detectDevice = adapter.getItem(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case DeviceDiscovery.REQUEST_ENABLE:{
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getContext(),"Bluetoothを起動しました",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Bluetooth起動がキャンセルされました", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class AsyncConnectDevice extends AsyncTask<Void, Void, Integer>{

        private ProgressDialog progressDialog;
        private BluetoothDevice device;

        private Exception exception;

        private AsyncConnectDevice(BluetoothDevice device){
            this.device = device;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext(), ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(device.getName());
            progressDialog.setMessage("接続中...");
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try{
                ConnectManager.connect(detectDevice);
            }catch (IOException e){
                e.printStackTrace();
                exception = e;
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            if(result > 0){
                progressDialog.dismiss();
                builder.setTitle("接続完了");
                builder.setMessage(device.getName() + "に接続しました");
                builder.create().show();
            }else {
                if(exception != null) {
                    builder.setMessage(exception.getMessage());
                    builder.setTitle("エラー");
                    builder.create().show();
                }
            }
        }
    }
}
