package com.nomadev.direc.ui.home.byname;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemByNameBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.DetailActivity;
import com.nomadev.direc.ui.home.HomeActivity;

import java.util.ArrayList;

public class ByNameAdapter extends RecyclerView.Adapter<ByNameAdapter.ViewHolder> {

    private ArrayList<PasienModel> listPasien = new ArrayList<>();

    public ByNameAdapter(ArrayList<PasienModel> listPasien) {
        this.listPasien = listPasien;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemByNameBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listPasien.get(position));
    }

    @Override
    public int getItemCount() {
        return listPasien.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final String NAMA = "nama";
        private final String GENDER = "gender";
        private final String TELEPON = "telepon";
        private final String ALAMAT = "alamat";
        private final String TANGGAL_LAHIR = "tanggal_lahir";

        private final ItemByNameBinding binding;

        private String nama, kelamin, telepon, alamat, tanggalLahir;

        public ViewHolder(@NonNull ItemByNameBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PasienModel pasienModel) {
            nama = pasienModel.getNama();
            kelamin = pasienModel.getKelamin();
            telepon = pasienModel.getTelepon();
            alamat = pasienModel.getAlamat();
            tanggalLahir = pasienModel.getTanggalLahir();

            binding.tvNama.setText(nama);
            binding.tvUsia.setText(kelamin);
            binding.cvPasien.setOnClickListener(v -> intentToDetail());
        }

        private void intentToDetail() {
            Toast.makeText(itemView.getContext(), "Ini Fungsi Intent", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
            intent.putExtra(NAMA, nama);
            intent.putExtra(GENDER, kelamin);
            intent.putExtra(TELEPON, telepon);
            intent.putExtra(ALAMAT, alamat);
            intent.putExtra(TANGGAL_LAHIR, tanggalLahir);
            itemView.getContext().startActivity(intent);
        }
    }
}
