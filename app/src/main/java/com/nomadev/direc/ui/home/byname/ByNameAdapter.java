package com.nomadev.direc.ui.home.byname;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemByNameBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.DetailActivity;
import com.viethoa.RecyclerViewFastScroller;

import java.util.ArrayList;

public class ByNameAdapter extends RecyclerView.Adapter<ByNameAdapter.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {

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

    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= listPasien.size())
            return null;

        String name = listPasien.get(pos).getNama();
        if (name == null || name.length() < 1)
            return null;

        return listPasien.get(pos).getNama().substring(0, 1);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final String NAMA = "nama";
        private final String GENDER = "gender";
        private final String TELEPON = "telepon";
        private final String ALAMAT = "alamat";
        private final String TANGGAL_LAHIR = "tanggal_lahir";
        private final String ID = "id";

        private final ItemByNameBinding binding;

        private String nama, kelamin, telepon, alamat, tanggalLahir, id;

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
            id = pasienModel.getId();

            binding.tvNama.setText(nama);
            binding.tvUsia.setText(kelamin);
            binding.cvPasien.setOnClickListener(v -> {
                Log.d("ID_ADAPTER", id);
                intentToDetail(nama, kelamin, telepon, alamat, tanggalLahir, id);
            });
        }

        private void intentToDetail(String nama, String kelamin, String telepon, String alamat, String tanggalLahir, String id) {
            Toast.makeText(itemView.getContext(), "Ini Fungsi Intent", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
            intent.putExtra(NAMA, nama);
            intent.putExtra(GENDER, kelamin);
            intent.putExtra(TELEPON, telepon);
            intent.putExtra(ALAMAT, alamat);
            intent.putExtra(TANGGAL_LAHIR, tanggalLahir);
            intent.putExtra(ID, id);
            itemView.getContext().startActivity(intent);
        }
    }
}
