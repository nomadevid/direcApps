package com.nomadev.direc.ui.home.byname;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ItemByNameBinding;
import com.nomadev.direc.databinding.ItemByNameHeaderBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.DetailActivity;
import com.viethoa.RecyclerViewFastScroller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ByNameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    private final ArrayList<PasienModel> listPasien;

    public ByNameAdapter(ArrayList<PasienModel> listPasien) {
        this.listPasien = listPasien;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SECTION_VIEW) {
            return new HeaderViewHolder(ItemByNameHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        return new ItemViewHolder(ItemByNameBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            HeaderViewHolder sectionHeaderViewHolder = (HeaderViewHolder) holder;
            sectionHeaderViewHolder.bind(listPasien.get(position));
            return;
        }

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.bind(listPasien.get(position));
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

    // HEADER VIEW HOLDER
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final ItemByNameHeaderBinding binding;

        public HeaderViewHolder(ItemByNameHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PasienModel headerItem) {
            binding.headerTitleTextview.setText(headerItem.getNama());
        }
    }

    // ITEM VIEW HOLDER
    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemByNameBinding binding;

        private String nama, kelamin, telepon, alamat, tanggalLahir, id, ageString;

        public ItemViewHolder(@NonNull ItemByNameBinding binding) {
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

            calculateAge(tanggalLahir);

            binding.tvNama.setText(nama);
            binding.tvUsia.setText(itemView.getContext().getString(R.string.usia_terisi, ageString));
            binding.cvPasien.setOnClickListener(v -> {
                Log.d("ID_ADAPTER", id);
                intentToDetail(nama, kelamin, telepon, alamat, tanggalLahir, id);
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

                    ageString = String.valueOf(age);
                    Log.d("usia", ageString);
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        }

        private void intentToDetail(String nama, String kelamin, String telepon, String alamat, String tanggalLahir, String id) {
            Toast.makeText(itemView.getContext(), "Ini Fungsi Intent", Toast.LENGTH_SHORT).show();
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
