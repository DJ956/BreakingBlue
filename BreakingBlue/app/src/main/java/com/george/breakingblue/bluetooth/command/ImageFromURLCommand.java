package com.george.breakingblue.bluetooth.command;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.george.breakingblue.bluetooth.session.ConnectManager;
import com.george.breakingblue.bluetooth.session.SessionTools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * URLから画像をダンロードするためのコマンド
 */
public class ImageFromURLCommand extends Command<String, Void> {

    private static final String READ_END_MESSAGE = "bt socket closed, read return: -1";

    private Activity activity;

    public ImageFromURLCommand(Activity activity, String commandData){
        super(commandData, CommandType.ImageFromURL);
        this.activity = activity;
    }

    @Override
    public void execute(SessionTools sessionTools){
        new AsyncExecuteDownload(activity, sessionTools).execute(commandData);
    }

    private class AsyncExecuteDownload extends AsyncTask<String, Integer, Integer>{

        private SessionTools sessionTools;
        private Activity activity;
        private ProgressDialog progressDialog;

        private List<byte[]> imageDataList;

        public AsyncExecuteDownload(Activity activity, SessionTools sessionTools){
            this.activity = activity;
            this.sessionTools = sessionTools;

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("しばらくお待ちください");
            progressDialog.setMessage("画像データダウンロード中...");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            if(!ConnectManager.isConnected()){
                try {
                    ConnectManager.connect(ConnectManager.getDevice());
                    sessionTools = ConnectManager.getSessionTools();
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = null;
            try{
                sessionTools.getOutputStream().write(commandType.toString().getBytes());
                sessionTools.getOutputStream().flush();

                sessionTools.getOutputStream().write(commandData.getBytes());
                sessionTools.getOutputStream().flush();

                byte[] sumBuffer = new byte[24];
                int sumLen = sessionTools.getInputStream().read(sumBuffer);
                String sumStr = new String(sumBuffer, 0, sumLen);
                Integer sum = Integer.parseInt(sumStr);

                System.out.println("Data sum:" + sum);

                byte[] buffer = new byte[1024];
                byteArrayOutputStream = new ByteArrayOutputStream();
                BufferedInputStream inputStream = new BufferedInputStream(sessionTools.getInputStream());
                int len;
                int total = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    total += buffer.length;
                    byteArrayOutputStream.write(buffer, 0, len);
                    double percentage = (double)total / sum * 100;
                    publishProgress((int)percentage);
                }
            }catch (IOException e) {
                if (e.getMessage().equals(READ_END_MESSAGE)) {
                    try {
                        byteArrayOutputStream.flush();
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                        imageDataList = (List<byte[]>) objectInputStream.readObject();
                        return 1;
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                        return 0;
                    }
                }else {
                    e.printStackTrace();
                    return 0;
                }

            }finally {
                sessionTools.close();
                ConnectManager.disconnect();
            }
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            progressDialog.dismiss();

            if(integer > 0){
                Toast.makeText(activity.getApplicationContext(), "ダウンロード完了", Toast.LENGTH_SHORT).show();
                new AsyncSaveFiles(activity, imageDataList).execute();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Error");
                builder.setMessage("ダウンロードに失敗しました");
                builder.create().show();
            }
        }
    }

    private class AsyncSaveFiles extends AsyncTask<Void, Integer, Integer>{

        private List<byte[]> imageDataList;

        private Exception exception;

        private Activity activity;
        private ProgressDialog progressDialog;

        private File savePath;

        private AsyncSaveFiles(Activity activity, List<byte[]> imageDataList){
            this.activity = activity;
            this.imageDataList = imageDataList;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(imageDataList.size());
            progressDialog.setProgress(0);
            progressDialog.setTitle("ファイル保存");
            progressDialog.setMessage("ファイル保存中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            try{
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.JAPAN);
                 savePath = new File(path, dateFormat.format(new Date()));

                if(savePath.mkdir()) {
                    int index = 1;
                    for (byte[] imgData : imageDataList) {
                        File file = new File(savePath, File.separator + String.valueOf(index));
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(imgData);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        publishProgress(index);

                        index++;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
                exception = e;
                return 0;
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
                builder.setTitle("保存完了");
                builder.setMessage(savePath.getPath() +  "にすべてのファイルを保存しました");
            }else {
                builder.setTitle("Error");
                builder.setMessage(exception.getMessage());
            }
            builder.create().show();
        }
    }

}
