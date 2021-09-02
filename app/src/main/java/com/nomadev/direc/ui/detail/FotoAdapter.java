package com.nomadev.direc.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemPhotoBinding;
import com.nomadev.direc.model.FotoModel;

import java.util.ArrayList;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder> {

    private ArrayList<FotoModel> fotoModelList;

    public FotoAdapter(ArrayList<FotoModel> fotoModelArrayList) {
        this.fotoModelList = fotoModelArrayList;
    }

    public ArrayList<FotoModel> getFotoModelList() {
        return fotoModelList;
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
        holder.bind(fotoModelList.get(position), position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ItemPhotoBinding binding;
        private int position;
        private FotoModel fotoModel;

        public ViewHolder(@NonNull ItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(v.getContext(), "Gambar Telah Dihapus",Toast.LENGTH_SHORT).show();
                    removeItem(position);
                    return true;
                }
            });
        }


        public void bind(FotoModel fotoModel, int position) {
            binding.ivPhoto.setImageURI(fotoModel.getFoto());
            this.position = position;
        }
    }

    public void removeItem(int position) {
        fotoModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, fotoModelList.size());
    }
}
