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
import com.george.breakingblue.fragment.command.ImageDownloadFromURLFragment;

/**
 * Web関連のコマンドをlistで表示させるクラス
 */

public class WebCommandListFragment extends Fragment {

    private ListView web_command_list_view;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_command_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        web_command_list_view = (ListView)view.findViewById(R.id.web_command_list_view);
        adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.web_command_item));
        web_command_list_view.setAdapter(adapter);
        web_command_list_view.setOnItemClickListener(new OnWebCommandListViewItemClick());
    }

    private class OnWebCommandListViewItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String command = adapter.getItem(position);

            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = null;

            if(command.equals(getString(R.string.image_from_url))){
                fragment = new ImageDownloadFromURLFragment();
            }

            transaction.replace(R.id.content, fragment);
            transaction.addToBackStack("back");
            transaction.commit();
        }
    }
}
