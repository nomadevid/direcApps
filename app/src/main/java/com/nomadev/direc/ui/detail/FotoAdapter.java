package com.nomadev.direc.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemPhotoBinding;
import com.nomadev.direc.model.FotoModel;

import java.util.ArrayList;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder> {

    private List<FotoModel> fotoModelList;

    public FotoAdapter(ArrayList<FotoModel> fotoModelArrayList) {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public int getItemCount() {
        return fotoModelList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(fotoModelList.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ItemPhotoBinding binding;
        private FotoModel fotoModel;

        public ViewHolder(@NonNull ItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(FotoModel fotoModel) {
            binding.ivPhoto.setImageURI(fotoModel.getFoto());
        }
    }
}
