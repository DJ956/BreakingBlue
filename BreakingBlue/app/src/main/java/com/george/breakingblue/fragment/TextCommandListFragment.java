package com.george.breakingblue.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.george.breakingblue.R;

/**
 * Created by GEORGE on 2017/06/09.
 */

public class TextCommandListFragment extends Fragment {

    private ListView text_command_list_view;

    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_command_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text_command_list_view = (ListView)view.findViewById(R.id.text_command_list_view);
        adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.text_command_item));
        text_command_list_view.setAdapter(adapter);
    }

    private class OnTextCommandListViewItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        }
    }
}
