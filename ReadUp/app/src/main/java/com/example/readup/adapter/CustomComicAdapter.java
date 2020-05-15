package com.example.readup.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.readup.R;

import java.util.ArrayList;

public class CustomComicAdapter extends ArrayAdapter<Uri> {

    private ArrayList<Uri> uris;
    private Activity context;

    public CustomComicAdapter(Activity context, ArrayList<Uri> uriarray) {
        super(context, R.layout.comic_display,uriarray);

        this.context = context;
        this.uris = uriarray;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v  = convertView;
        ViewHolder vh  = null;
        if(v == null){
            LayoutInflater li = context.getLayoutInflater();
            v=li.inflate(R.layout.comic_display,null,true);
            vh = new ViewHolder(v);
            v.setTag(vh);
        }else{
            vh = (ViewHolder)v.getTag();
        }

        vh.iv.setImageURI(uris.get(position));

        return v;
    }

    public void insert(@Nullable Uri object) {
        uris.add(object);
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView iv;

        ViewHolder(View v){
            iv = v.findViewById(R.id.comic_img);
        }
    }

    public void Delete(int index){
        uris.remove(index);
        notifyDataSetChanged();
    }

    public ArrayList<Uri> getAllUri(){
        return uris;
    }
}
