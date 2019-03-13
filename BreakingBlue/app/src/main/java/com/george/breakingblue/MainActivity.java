package com.george.breakingblue;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.george.breakingblue.fragment.CommandRootFragment;
import com.george.breakingblue.fragment.ConnectFragment;

public class MainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {

    private TabHost.TabSpec connectTab;
    private TabHost.TabSpec commandTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTabHost host = (FragmentTabHost) findViewById(android.R.id.tabhost);
        host.setup(this, getSupportFragmentManager(), R.id.content);

        connectTab = host.newTabSpec("connect").setIndicator("接続");

        commandTab = host.newTabSpec("command").setIndicator("コマンドリスト");

        host.addTab(connectTab, ConnectFragment.class, null);
        host.addTab(commandTab, CommandRootFragment.class, null);

        host.setOnTabChangedListener(this);
    }

    @Override
    public void onTabChanged(String tabId) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment;
        if(commandTab.getTag().equals(tabId)){
            fragment = new CommandRootFragment();
            transaction.replace(R.id.content, fragment);
        }else if(connectTab.getTag().equals(tabId)){
            fragment = new ConnectFragment();
            transaction.replace(R.id.content, fragment);
        }

        transaction.commit();
    }
}
