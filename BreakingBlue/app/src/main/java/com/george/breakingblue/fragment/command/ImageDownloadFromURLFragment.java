package com.george.breakingblue.fragment.command;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.george.breakingblue.R;
import com.george.breakingblue.bluetooth.command.ImageFromURLCommand;
import com.george.breakingblue.bluetooth.session.ConnectManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * URLを送信し画像ファイルをダウンロードするフラグメント
 */
public class ImageDownloadFromURLFragment extends Fragment {

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private static final String DEFAULT_URL = "https://www.google.co.jp";

    private WebView webView;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_from_url_command, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = (SearchView)view.findViewById(R.id.image_from_url_search_view);
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new OnQueryTextListener());

        final TextView textView = (TextView)view.findViewById(R.id.image_from_url_textView);

        webView = (WebView)view.findViewById(R.id.image_from_url_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                textView.setText(url);
            }
        });
        webView.loadUrl(DEFAULT_URL);

        Button detectButton = (Button)view.findViewById(R.id.button_detect_url);
        detectButton.setOnClickListener(new OnDetectButtonClick());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }


    private class OnQueryTextListener implements SearchView.OnQueryTextListener{
        @Override
        public boolean onQueryTextSubmit(String words) {
            if(words !=  null && !words.isEmpty()){
                searchView.setIconified(false);
                searchView.clearFocus();
                if(words.contains("http://") || words.contains("https://")){
                    webView.loadUrl(words);
                }else {
                    try{
                        webView.loadUrl(DEFAULT_URL + "/webhp?source=search_app&gws_rd=ssl#q=" +
                                URLEncoder.encode(words, "UTF-8"));
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                }

            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    }

    private class OnDetectButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String url = webView.getUrl();
            if(ConnectManager.getSessionTools() != null){
                ImageFromURLCommand imageFromURLCommand = new ImageFromURLCommand(getActivity(), url);
                imageFromURLCommand.execute(ConnectManager.getSessionTools());
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Error");
                builder.setMessage("デバイスに接続されていません");
                builder.create().show();
            }
        }
    }
}
