package com.nomadev.direc.ui.detail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemPhotoBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class FotoStreamAdapter extends RecyclerView.Adapter<FotoStreamAdapter.ViewHolder> {

    private ArrayList<String> listImageUrl;

    public FotoStreamAdapter(ArrayList<String> listData) {
        this.listImageUrl = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FotoStreamAdapter.ViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public int getItemCount() {
        return listImageUrl.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listImageUrl.get(position), position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ItemPhotoBinding binding;
        private int position;
        private ArrayList<String> listUrl;

        public ViewHolder(@NonNull ItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String listImageUrl, int position) {
            this.position = position;
//            listUrl = new ArrayList<>();
//            listUrl = hasilPeriksaModel.getUrlString();
//            Log.d("URL", "bind: " + hasilPeriksaModel.getUrlString());
//            String imageUrl = (String) listUrl.get(position);
//            Log.d("URL", "String: " + listUrl.get(position));
//            new FetchImage(listImageUrl).start();
            Picasso.get().load(listImageUrl).into(binding.ivPhoto);
        }

//        class FetchImage extends Thread {
//
//            String URL;
//            Bitmap bitmap;
//
//            FetchImage(String URL) {
//                this.URL = URL;
//            }
//
//            @Override
//            public void run() {
//                InputStream inputStream = null;
//                try {
//                    inputStream = new URL(URL).openStream();
//                    bitmap = BitmapFactory.decodeStream(inputStream);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                binding.ivPhoto.setImageBitmap(bitmap);
//            }
//        }

    }
}
