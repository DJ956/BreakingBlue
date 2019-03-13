package com.george.breakingblue.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.george.breakingblue.R;
import com.george.breakingblue.fragment.command.CaptureImageCommandFragment;

/**
 * カメラを使ったコマンドを選択するフラグメント
 */
public class CameraCommandListFragment extends Fragment {

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera_command_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView cameraCommandListView = (ListView)view.findViewById(R.id.camera_command_list_view);
        adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.camera_command_item));
        cameraCommandListView.setAdapter(adapter);
        cameraCommandListView.setOnItemClickListener(new OnCameraCommandListViewItemClick());
    }

    private class OnCameraCommandListViewItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String command = adapter.getItem(position);

            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = null;

            if(command.equals(getString(R.string.capture_and_send_image_camera_cmd))){
                fragment = new CaptureImageCommandFragment();
            }else if(command.equals(getString(R.string.read_qr_code_and_send_camera_cmd))){

            }else if(command.equals(getString(R.string.read_bar_code_and_send_camera_cmd))){

            }

            transaction.replace(R.id.content, fragment);
            transaction.addToBackStack("back");
            transaction.commit();
        }
    }
}
