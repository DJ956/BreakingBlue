package com.george.breakingblue.bluetooth.command;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.george.breakingblue.bluetooth.session.ConnectManager;
import com.george.breakingblue.bluetooth.session.SessionTools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * キャプチャーした画像データをPCに送信するコマンド
 */
public class CaptureImageAndSendCommand extends Command<List<Uri>, Void> {

    private Activity activity;

    public CaptureImageAndSendCommand(Activity activity, List<Uri> commandData){
        super(commandData, CommandType.ViewImage);
        this.activity = activity;
    }

    @Override
    public void execute(SessionTools sessionTools) {
        new AsyncExecute(activity, sessionTools).execute();
    }

    private class AsyncExecute extends AsyncTask<Void, Integer, Integer>{

        private ProgressDialog progressDialog;
        private Activity activity;
        private SessionTools sessionTools;
        private Exception exception;

        private AsyncExecute(Activity activity, SessionTools sessionTools){
            this.activity = activity;
            this.sessionTools = sessionTools;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("しばらくお待ちください");
            progressDialog.setMessage("データ送信中...");
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if(!ConnectManager.isConnected()){
                try {
                    ConnectManager.connect(ConnectManager.getDevice());
                    sessionTools = ConnectManager.getSessionTools();
                } catch (IOException e) {
                    exception = e;
                    e.printStackTrace();
                    return 0;
                }
            }

            //データ受信はいくらでも行けるが、送信に関しては分割する必要がある。
            try{
                sessionTools.getOutputStream().write(commandType.toString().getBytes());
                sessionTools.getOutputStream().flush();

                //総データ取得
                List<Integer> sizeList = getDataSizeListByUriList(commandData);
                int sum = 0;
                for (int size: sizeList){
                    sum += size;
                }
                //総データ送信
                sessionTools.getOutputStream().write(String.valueOf(sum).getBytes());
                sessionTools.getOutputStream().flush();

                //各ファイルのデータサイズを送信
                for(int size: sizeList){
                    sessionTools.getOutputStream().write(String.valueOf(size).getBytes());
                    sessionTools.getOutputStream().flush();
                }


                //データをファイルごとに送信する
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(sessionTools.getOutputStream());
                for(Uri imageUri : commandData){
                    //イメージのバイナリー配列を取得する
                    ByteArrayOutputStream imageByteArrayStream = new ByteArrayOutputStream();
                    InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageByteArrayStream);

                    //データを分割して送信する
                    byte[] data = imageByteArrayStream.toByteArray();
                    sum = imageByteArrayStream.toByteArray().length;
                    int divide = 1024;
                    int divisionCount = sum / divide;
                    int remainder = sum % divide;


                    if(divisionCount < 0){
                        bufferedOutputStream.write(data);
                        bufferedOutputStream.flush();
                    }else {
                        for (int index = 0; index < divisionCount; index++) {
                            bufferedOutputStream.write(data, index * divide, divide);
                            bufferedOutputStream.flush();

                            double percentage = (double) (index * divide) / sum * 100;
                            publishProgress((int) percentage);
                        }
                    }

                    if(remainder > 0){
                        bufferedOutputStream.write(data, sum - remainder, remainder);
                        bufferedOutputStream.flush();
                        publishProgress(100);
                    }

                    bitmap.recycle();
                    bitmap = null;

                }
            }catch (IOException e){
                e.printStackTrace();
                exception = e;
                return 0;
            }finally {
                ConnectManager.disconnect();
                sessionTools.close();
            }

            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            progressDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if(integer > 0){
                builder.setTitle("データ送信完了");
                builder.setMessage("データ送信に成功しました");
            }else {
                builder.setTitle("データ送信に失敗しました");
                if(exception != null){
                    builder.setMessage(exception.getMessage());
                }else {
                    builder.setMessage("原因不明のエラー発生");
                }
            }
            builder.create().show();
        }
    }


    private List<Integer> getDataSizeListByUriList(List<Uri> uriList){
        List<Integer> result = new ArrayList<>();
        for(Uri imageUri : uriList){
            try(InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(bitmap != null){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    result.add(byteArrayOutputStream.toByteArray().length);

                    bitmap.recycle();
                    bitmap = null;
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        return result;
    }

}
