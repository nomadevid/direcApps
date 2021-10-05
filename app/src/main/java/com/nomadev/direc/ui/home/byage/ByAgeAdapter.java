package com.nomadev.direc.ui.home.byage;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ItemByAgeBinding;
import com.nomadev.direc.databinding.ItemByAgeHeaderBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.DetailActivity;

import java.util.ArrayList;

public class ByAgeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    private final ArrayList<PasienModel> listPasien;

    public ByAgeAdapter(ArrayList<PasienModel> listPasien) {
        this.listPasien = listPasien;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SECTION_VIEW) {
            return new ByAgeAdapter.HeaderViewHolder(ItemByAgeHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        return new ByAgeAdapter.ItemViewHolder(ItemByAgeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (listPasien.get(position).isSection) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (SECTION_VIEW == getItemViewType(position)) {
            ByAgeAdapter.HeaderViewHolder sectionHeaderViewHolder = (ByAgeAdapter.HeaderViewHolder) holder;
            sectionHeaderViewHolder.bind(listPasien.get(position));
            return;
        }

        ByAgeAdapter.ItemViewHolder itemViewHolder = (ByAgeAdapter.ItemViewHolder) holder;
        itemViewHolder.bind(listPasien.get(position));
    }

    @Override
    public int getItemCount() {
        return listPasien.size();
    }

    // HEADER VIEW HOLDER
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final ItemByAgeHeaderBinding binding;

        public HeaderViewHolder(ItemByAgeHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PasienModel headerItem) {
            binding.headerTitleTextview.setText(headerItem.getTanggalLahir());
        }
    }

    // ITEM VIEW HOLDER
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemByAgeBinding binding;

        private String nama, kelamin, telepon, alamat, tanggalLahir, id;
        private int kelaminInteger;

        public ItemViewHolder(@NonNull ItemByAgeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PasienModel pasienModel) {

            nama = pasienModel.getNama();
            kelaminInteger = pasienModel.getKelamin();
            kelamin = String.valueOf(kelaminInteger);
            telepon = pasienModel.getTelepon();
            alamat = pasienModel.getAlamat();
            tanggalLahir = pasienModel.getTanggalLahir();
            id = pasienModel.getId();

            binding.tvNama.setText(nama);
            binding.tvUsia.setText(itemView.getContext().getString(R.string.usia_terisi, tanggalLahir));
            binding.cvPasien.setOnClickListener(v -> {
                Log.d("ID_ADAPTER", id);
                intentToDetail(nama, kelamin, telepon, alamat, tanggalLahir, id);
            });
        }

        private void intentToDetail(String nama, String kelamin, String telepon, String alamat, String tanggalLahir, String id) {
            Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
            intent.putExtra(DetailActivity.NAMA, nama);
            intent.putExtra(DetailActivity.GENDER, kelamin);
            intent.putExtra(DetailActivity.TELEPON, telepon);
            intent.putExtra(DetailActivity.ALAMAT, alamat);
            intent.putExtra(DetailActivity.TANGGAL_LAHIR, tanggalLahir);
            intent.putExtra(DetailActivity.ID, id);
            itemView.getContext().startActivity(intent);
        }
    }
}

