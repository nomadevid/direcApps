package com.nomadev.direc.ui.detail;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nomadev.direc.databinding.ItemPhotoBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FotoStreamUpdateAdapater extends RecyclerView.Adapter<FotoStreamUpdateAdapater.ViewHolder> {

    private final ArrayList<String> listImageUrl;
    private final String id_pasien;
    private final String id_data;

    public FotoStreamUpdateAdapater(ArrayList<String> foto, String id_pasien, String id_data) {
        this.listImageUrl = foto;
        this.id_data = id_data;
        this.id_pasien = id_pasien;
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

        private final ItemPhotoBinding binding;
        private int position;
        private final FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(@NonNull ItemPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnLongClickListener(v -> {
                deleteImage(v);
                removeItem(position);
                return true;
            });
        }

        @SuppressLint("NotifyDataSetChanged")
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
            deleteImage.delete().addOnSuccessListener(unused -> {
                Toast.makeText(v.getContext(), "Foto Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                Log.d("SUCCESS", "onSuccess: " + listImageUrl);
            }).addOnFailureListener(e -> {
                Toast.makeText(v.getContext(), "Foto Gagal Dihapus", Toast.LENGTH_SHORT).show();
                Log.d("FAIL", "onFailur: " + e.toString());
            });

            DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);
            dbData.update(
                    "foto", FieldValue.arrayRemove(listImageUrl.get(position))
            ).addOnSuccessListener(unused -> Log.d("SUCCESS", "Field Value Reset: "))
                    .addOnFailureListener(e -> Log.d("FAILURE", "ERROR : " + e.toString()));
        }
    }
}
