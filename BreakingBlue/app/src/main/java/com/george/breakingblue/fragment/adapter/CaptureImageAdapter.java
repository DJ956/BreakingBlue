package com.george.breakingblue.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.george.breakingblue.R;

import java.util.List;


/**
 * キャプチャーした画像を表示させるアダプター
 */
public class CaptureImageAdapter extends ArrayAdapter<Bitmap> {

    private LayoutInflater inflater;
    private int resource;

    public CaptureImageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Bitmap> objects) {
        super(context, resource, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView != null){
            view = convertView;
        }else {
            view = inflater.inflate(resource, null);
        }

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView_capture);
        if(getItem(position) != null){
            imageView.setImageBitmap(getItem(position));
        }else {
            imageView.setImageBitmap(null);
        }

        return view;
    }
}
