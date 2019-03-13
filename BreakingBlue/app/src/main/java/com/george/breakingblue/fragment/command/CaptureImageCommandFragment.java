package com.george.breakingblue.fragment.command;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.george.breakingblue.R;
import com.george.breakingblue.bluetooth.command.CaptureImageAndSendCommand;
import com.george.breakingblue.bluetooth.session.ConnectManager;
import com.george.breakingblue.fragment.adapter.CaptureImageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * カメラを起動させてPCに画像を送信するフラグメント
 */
public class CaptureImageCommandFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final int THUMBNAIL_SIZE = 400;

    private List<Uri> uriList;

    private static final int RESULT_CAMERA = 10;

    private Uri imagePath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_capture_image_command, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView capture_image_list_view = (GridView) view.findViewById(R.id.capture_grid_view);
        uriList = new ArrayList<>();
        CaptureImageAdapter captureImageAdapter = new CaptureImageAdapter(getContext(), R.layout.image_list, new ArrayList<Bitmap>());
        capture_image_list_view.setAdapter(captureImageAdapter);
        capture_image_list_view.setOnItemClickListener(new OnItemCaptureImageViewClick());

        Button start_camera_app_button = (Button)view.findViewById(R.id.button_start_camera_app);
        start_camera_app_button.setOnClickListener(new OnStartCameraAppButtonClick());

        Button send_captured_button = (Button)view.findViewById(R.id.button_captured_image_send);
        send_captured_button.setOnClickListener(new SendCaptureButtonClick());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    private class OnStartCameraAppButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            imagePath = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
            startActivityForResult(intent, RESULT_CAMERA);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_CAMERA && resultCode == Activity.RESULT_OK) {
            if (imagePath != null) {
                uriList.add(imagePath);
            }
        }
    }

    private class SendCaptureButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(ConnectManager.getSessionTools() != null){
                CaptureImageAndSendCommand captureImageAndSendCommand = new CaptureImageAndSendCommand(getActivity(), uriList);
                captureImageAndSendCommand.execute(ConnectManager.getSessionTools());
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Error");
                builder.setMessage("デバイスに接続されていません");
                builder.create().show();
            }
        }
    }

    private class OnItemCaptureImageViewClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        }
    }
}
