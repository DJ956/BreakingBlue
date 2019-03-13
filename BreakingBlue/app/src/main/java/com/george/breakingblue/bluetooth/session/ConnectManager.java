package com.george.breakingblue.bluetooth.session;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by GEORGE on 2017/06/08.
 */

public class ConnectManager {
    //private static final UUID APP_UUID = UUID.fromString("11111111-1111-1111-1111-111111111123");
    private static final UUID APP_UUID = UUID.fromString("00000000-0000-1105-8000-00805F9B34FB");

    private static BluetoothDevice device = null;

    private static SessionTools sessionTools = null;

    private static BluetoothSocket socket = null;

    private ConnectManager(){
    }

    public static void connect(BluetoothDevice device) throws IOException {
        if(ConnectManager.device == null){
            ConnectManager.device = device;
        }
        //BluetoothSocket socket = ConnectManager.device.createRfcommSocketToServiceRecord(APP_UUID);
        socket = ConnectManager.device.createInsecureRfcommSocketToServiceRecord(APP_UUID);
        socket.connect();
        sessionTools = new SessionTools(socket.getInputStream(), socket.getOutputStream());
    }

    public static void setDevice(BluetoothDevice device){
        ConnectManager.device = device;
    }

    public static BluetoothDevice getDevice(){
        return device;
    }

    public static SessionTools getSessionTools(){
        return sessionTools;
    }

    public static boolean isConnected(){
        if(socket != null){
            return socket.isConnected();
        }else {
            return false;
        }
    }

    public static void disconnect(){
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
