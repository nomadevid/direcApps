package com.nomadev.direc.ui.detail;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nomadev.direc.databinding.ItemPhotoBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FotoStreamUpdateAdapater extends RecyclerView.Adapter<FotoStreamUpdateAdapater.ViewHolder> {

    private ArrayList<String> listImageUrl;

    public FotoStreamUpdateAdapater(ArrayList<String> foto) {
        this.listImageUrl = foto;
    }

    public ArrayList<String> getListImageUrl() {
        return listImageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FotoStreamUpdateAdapater.ViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        private FirebaseFirestore db;

        public ViewHolder(@NonNull ItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnLongClickListener(v -> {
                deleteImage(v);
                removeItem(position);
                return true;
            });
        }

        private void removeItem(int position) {
            listImageUrl.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listImageUrl.size());
            notifyDataSetChanged();
        }

        public void bind(String listImageUrl, int position) {
            this.position = position;
            Picasso.get().load(listImageUrl).into(binding.ivPhoto);
        }

        public void deleteImage(View v) {
            StorageReference deleteImage = FirebaseStorage.getInstance().getReferenceFromUrl(listImageUrl.get(position));
            deleteImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(v.getContext(), "Foto Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                    Log.d("SUCCESS", "onSuccess: " + listImageUrl);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(v.getContext(), "Foto Gagal Dihapus", Toast.LENGTH_SHORT).show();
                Log.d("FAIL", "onFailur: " + e.toString());
            });

        }
    }
}
