package com.nomadev.direc.ui.detail;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private String nama, tanggal;

    public FotoStreamAdapter(ArrayList<String> listData, String nama, String tanggal) {
        this.listImageUrl = listData;
        this.nama = nama;
        this.tanggal = tanggal;
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
        private String url;
        private final String URL_FOTO = "url";
        private final String NAMA = "nama";
        private final String TANGGAL_PERIKSA = "tanggal_periksa";
        private FragmentActivity fragmentActivity;
        private FragmentManager fragmentManager;
        private ArrayList<String> listUrl;

        public ViewHolder(@NonNull ItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                fragmentActivity = (FragmentActivity) (v.getContext());
                fragmentManager = fragmentActivity.getSupportFragmentManager();
                DialogFotoActivity dialog = new DialogFotoActivity();
                Bundle bundle = new Bundle();
                bundle.putString(URL_FOTO, url);
                bundle.putString(NAMA, nama);
                Log.d("DIALOG", "ViewHolder: " + nama);
                bundle.putString(TANGGAL_PERIKSA, tanggal);
                dialog.setArguments(bundle);
                dialog.show(fragmentManager, "Dialog Edit Data");
                Log.d("TAG", "ViewHolder: Jalan");
            });
        }

        public void bind(String listImageUrl, int position) {
            this.position = position;
            url = listImageUrl;
            Picasso.get().load(listImageUrl).into(binding.ivPhoto);
        }

    }
}
