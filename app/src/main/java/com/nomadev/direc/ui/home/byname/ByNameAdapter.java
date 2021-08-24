package com.nomadev.direc.ui.home.byname;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemByNameBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.home.HomeActivity;

import java.util.ArrayList;

public class ByNameAdapter extends RecyclerView.Adapter<ByNameAdapter.ViewHolder> {

    private ArrayList<PasienModel> listPasien = new ArrayList<>();
    private Context context;

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

        private final ItemByNameBinding binding;

        public ViewHolder(@NonNull ItemByNameBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PasienModel pasienModel) {
            binding.tvNama.setText(pasienModel.getNama());
            binding.tvUsia.setText(pasienModel.getKelamin());
            binding.cvPasien.setOnClickListener(v -> intentToDetail());
        }

        private void intentToDetail() {
            Toast.makeText(itemView.getContext(), "Ini Fungsi Intent", Toast.LENGTH_SHORT).show();
        }
    }
}
