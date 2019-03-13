package com.george.breakingblue.bluetooth.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by GEORGE on 2017/06/06.
 */
public class SessionTools {

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public SessionTools(InputStream inputStream, OutputStream outputStream){
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public InputStream getInputStream(){
        return inputStream;
    }

    public OutputStream getOutputStream(){
        return outputStream;
    }

    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }

        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }
}
