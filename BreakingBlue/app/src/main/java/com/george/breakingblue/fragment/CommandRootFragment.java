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


/**
 * コマンドのジャンルをlistを使って表示させるクラス
 */
public class CommandRootFragment extends Fragment {

    private ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_command_root, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView commandListView = (ListView)view.findViewById(R.id.command_list_view);
        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.command_item));
        commandListView.setAdapter(arrayAdapter);
        commandListView.setOnItemClickListener(new OnCommandListViewItemClick());
    }

    private class OnCommandListViewItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String command = arrayAdapter.getItem(position);
            if(command == null){
                return;
            }

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment fragment = null;

            if(command.equals(getString(R.string.text_cmd))){
                fragment = new TextCommandListFragment();

            }else if(command.equals(getString(R.string.web_cmd))){
                fragment = new WebCommandListFragment();
            }else if(command.equals(getString(R.string.camera_cmd))){
                fragment = new CameraCommandListFragment();
            }

            fragmentTransaction.replace(R.id.content, fragment);
            fragmentTransaction.addToBackStack("Back");
            fragmentTransaction.commit();
        }
    }
}
