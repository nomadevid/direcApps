package com.nomadev.direc.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemHasilPeriksaPasienBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.PasienModel;

import java.util.ArrayList;

public class HasilPeriksaAdapter extends RecyclerView.Adapter<HasilPeriksaAdapter.ViewHolder> {

    private ArrayList<HasilPeriksaModel> listData = new ArrayList<>();

    public HasilPeriksaAdapter(ArrayList<HasilPeriksaModel> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemHasilPeriksaPasienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HasilPeriksaAdapter.ViewHolder holder, int position) {
        holder.bind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemHasilPeriksaPasienBinding binding;

        public ViewHolder(@NonNull ItemHasilPeriksaPasienBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind (HasilPeriksaModel hasilPeriksaModel){
            binding.tvHasilPeriksa.setText(hasilPeriksaModel.getHasil_periksa());
            binding.tvKeluhan.setText(hasilPeriksaModel.getKeluhan());
            binding.tvTerapi.setText(hasilPeriksaModel.getTerapi());
        }
    }
}
