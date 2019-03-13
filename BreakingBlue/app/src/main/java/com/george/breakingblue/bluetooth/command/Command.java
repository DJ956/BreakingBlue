package com.george.breakingblue.bluetooth.command;

import com.george.breakingblue.bluetooth.session.SessionTools;

import java.io.IOException;

/**
 *
 * @param <D> コマンドデータ
 * @param <R> 処理結果
 */
public abstract class Command<D, R> {
    protected CommandType commandType;
    protected D commandData;
    protected R resultData;
    protected boolean isFinished = false;
    protected boolean isError = false;


    public Command(D commandData, CommandType commandType){
        this.commandType = commandType;
        this.commandData = commandData;
    }

    public CommandType getCommandType(){
        return commandType;
    }

    public D getCommandData(){
        return commandData;
    }

    public R getResultData(){
        return resultData;
    }

    public void setResultData(R resultData){
        this.resultData = resultData;
    }

    public boolean isFinished(){
        return isFinished;
    }

    public boolean isError(){
        return isError;
    }

    public abstract void execute(SessionTools sessionTools);
}
