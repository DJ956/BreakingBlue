package com.george.breakingblue.fragment.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.george.breakingblue.R;

import java.util.List;

/**
 * Created by GEORGE on 2017/06/08.
 */

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater inflater;

    public DeviceAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<BluetoothDevice> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView != null){
            view = convertView;
        }else{
            view = inflater.inflate(R.layout.device_list, null);
        }

        TextView deviceNameTextView = (TextView)view.findViewById(R.id.device_name_textView);
        TextView deviceAddressTextView = (TextView)view.findViewById(R.id.device_address_textView);

        deviceNameTextView.setText(getItem(position).getName());
        deviceAddressTextView.setText(getItem(position).getAddress());

        return view;
    }
}
