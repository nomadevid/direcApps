package com.nomadev.direc.ui.search;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.databinding.ItemSearchBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.DetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final ArrayList<String> idList;
    private final ArrayList<PasienModel> searchList;

    public SearchAdapter(ArrayList<PasienModel> searchList, ArrayList<String> idList) {
        this.searchList = searchList;
        this.idList = idList;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchAdapter.ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        holder.bind(searchList.get(position), idList.get(position));
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemSearchBinding binding;
        private final String ID = "id";

        public ViewHolder(@NonNull ItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PasienModel data, String id) {
            calculateAge(data.getTanggalLahir());
            binding.tvNama.setText(data.getNama());
            binding.tvTelepon.setText(data.getTelepon());
            binding.tvAlamat.setText(data.getAlamat());
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                intent.putExtra(ID, id);
                itemView.getContext().startActivity(intent);
            });
        }

        private void calculateAge(String tanggalLahir) {
            // KONVERSI STRING KE DATE
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date date = format.parse(tanggalLahir);

                // HITUNG USIA
                Calendar dob = Calendar.getInstance();
                Calendar today = Calendar.getInstance();

                if (date != null) {
                    dob.setTime(date);
                    int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                        age--;
                    }

                    String ageString = String.valueOf(age);
                    Log.d("usia", ageString);
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        }
    }
}
