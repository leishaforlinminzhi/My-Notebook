package com.example.mynotebook.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.mynotebook.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private Context context;
    private List<Uri> photos;

    public PhotoAdapter(Context context, List<Uri> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
            holder = new ViewHolder();
            holder.imageViewPhoto = convertView.findViewById(R.id.imageViewPhoto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Uri photoUri = photos.get(position);
        Log.d(TAG, photoUri.toString());

        // 加载图片
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(photoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.imageViewPhoto.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewPhoto;
    }
}
