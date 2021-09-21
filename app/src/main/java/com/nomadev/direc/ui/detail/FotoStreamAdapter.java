package com.nomadev.direc.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemPhotoBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FotoStreamAdapter extends RecyclerView.Adapter<FotoStreamAdapter.ViewHolder> {

    private final ArrayList<String> listImageUrl;
    private final String nama;
    private final String tanggal;

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
        holder.bind(listImageUrl.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemPhotoBinding binding;
        private String url;
        public static final String URL_FOTO = "url";
        public static final String NAMA = "nama";
        public static final String TANGGAL_PERIKSA = "tanggal_periksa";
        private FragmentActivity fragmentActivity;
        private FragmentManager fragmentManager;

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

        public void bind(String listImageUrl) {
            url = listImageUrl;
            Picasso.get().load(listImageUrl).fit().into(binding.ivPhoto);
        }

    }
}
