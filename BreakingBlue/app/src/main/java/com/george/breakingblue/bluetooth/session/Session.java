package com.george.breakingblue.bluetooth.session;

import com.george.breakingblue.bluetooth.command.Command;

import java.io.*;

/**
 * 通信の接続、コマンドの実行、通信の切断を行う
 */
public class Session {

    private SessionTools sessionTools;
    private Command command;

    public Session(SessionTools sessionTools, Command command) throws IOException {
        this.sessionTools = sessionTools;
        this.command = command;
    }


    public final void execute() throws IOException {
        try {
            open();
            run();
            finish();
        } finally {
            close();
        }
    }

    /**
     * コマンドタイプを送信
     * @throws IOException
     */
    private void open() throws IOException{
        if(!ConnectManager.isConnected() && ConnectManager.getDevice() != null){
            sessionTools.close();
            ConnectManager.connect(ConnectManager.getDevice());
            sessionTools = ConnectManager.getSessionTools();
        }

        sessionTools.getOutputStream().write(command.getCommandType().toString().getBytes());
        sessionTools.getOutputStream().flush();
    }

    private void run() throws IOException {
        command.execute(sessionTools);
    }

    private void finish() throws IOException {
        //command.getCompleteTaskListener().execute();
    }

    private void close(){
        if(ConnectManager.isConnected()){
            ConnectManager.disconnect();
        }

        if(sessionTools != null){
            sessionTools.close();
        }
    }
}

