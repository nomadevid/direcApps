package com.nomadev.direc.ui.detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ItemHasilPeriksaPasienBinding;
import com.nomadev.direc.databinding.ItemHasilPeriksaPasienHeaderBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.ui.detail.dialogadddata.DialogUpdateDataActivity;
import com.nomadev.direc.ui.detail.dialogdeletedata.DialogDeleteDataActiivity;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HasilPeriksaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;

    private final ArrayList<HasilPeriksaModel> listData;

    public HasilPeriksaAdapter(ArrayList<HasilPeriksaModel> listData) {
        this.listData = listData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SECTION_VIEW) {
            return new HasilPeriksaAdapter.HeaderViewHolder(ItemHasilPeriksaPasienHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return new HasilPeriksaAdapter.ViewHolder(ItemHasilPeriksaPasienBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (listData.get(position).isSection) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (SECTION_VIEW == getItemViewType(position)) {
            HasilPeriksaAdapter.HeaderViewHolder sectionHeaderViewHolder = (HasilPeriksaAdapter.HeaderViewHolder) holder;
            sectionHeaderViewHolder.bind(listData.get(position));
            return;
        }

        HasilPeriksaAdapter.ViewHolder itemViewHolder = (HasilPeriksaAdapter.ViewHolder) holder;
        itemViewHolder.bind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    // HEADER VIEW HOLDER
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemHasilPeriksaPasienHeaderBinding binding;

        public HeaderViewHolder(@NonNull ItemHasilPeriksaPasienHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HasilPeriksaModel hasilPeriksaModel) {
            binding.headerTitleTextview.setText(dateHeaderFormat(hasilPeriksaModel.getTanggal()));
        }

        private String dateHeaderFormat(String input) {
            String dateHeader = "";
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                Date date = format.parse(input);
                SimpleDateFormat formatHeader = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
                if (date != null) {
                    dateHeader = formatHeader.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return dateHeader;
        }
    }

    // ITEM VIEW HOLDER
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private final ItemHasilPeriksaPasienBinding binding;
        private String id_pasien, id_data, tanggal_data, nama, skema1, skema2;
        public static final String ID_PASIEN = "id_pasien";
        public static final String ID_DATA = "id_data";
        public static final String TANGGAL_DATA = "tanggal_data";
        public static final String URL_FOTO = "url";
        public static final String NAMA = "nama";
        public static final String TANGGAL_PERIKSA = "tanggal_periksa";
        public static final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        private FragmentManager fragmentManager;

        public ViewHolder(@NonNull ItemHasilPeriksaPasienBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ibEditDown.setOnClickListener(this);
            binding.ivScheme1.setOnClickListener(v -> viewImage(v, skema1));
            binding.ivScheme2.setOnClickListener(v -> viewImage(v, skema2));
        }

        private void viewImage(View v, String skema) {
            FragmentActivity fragmentActivity = (FragmentActivity) (v.getContext());
            fragmentManager = fragmentActivity.getSupportFragmentManager();
            DialogFotoActivity dialog = new DialogFotoActivity();
            Bundle bundle = new Bundle();
            bundle.putString(URL_FOTO, skema);
            bundle.putString(NAMA, nama);
            Log.d("DIALOG", "ViewHolder: " + nama);
            bundle.putString(TANGGAL_PERIKSA, tanggal_data);
            dialog.setArguments(bundle);
            dialog.show(fragmentManager, "Dialog Edit Data");
            Log.d("TAG", "ViewHolder: Jalan");
        }

        public void bind(HasilPeriksaModel hasilPeriksaModel) {
            binding.tvHasilPeriksa.setText(hasilPeriksaModel.getHasil_periksa());
            binding.tvKeluhan.setText(hasilPeriksaModel.getKeluhan());
            binding.tvTerapi.setText(hasilPeriksaModel.getTerapi());
            binding.tvPemeriksa.setText(hasilPeriksaModel.getPemeriksa());
            binding.tvPenyakit.setText(hasilPeriksaModel.getPenyakit());
            binding.tvTagihan.setText(formatRupiah.format(hasilPeriksaModel.getTagihan()));
            id_pasien = hasilPeriksaModel.getId();
            id_data = hasilPeriksaModel.getId_data();
            tanggal_data = hasilPeriksaModel.getTanggal();
            skema1 = hasilPeriksaModel.getSkema1();
            skema2 = hasilPeriksaModel.getSkema2();
            setScheme(skema1, skema2);
            Log.d("TAG", "bind: " + hasilPeriksaModel.getUrlString());
            if (hasilPeriksaModel.getUrlString() != null) {
                setAdapter(hasilPeriksaModel.getUrlString());
            } else binding.rvPhoto.setVisibility(View.GONE);

            if ((hasilPeriksaModel.getUrlString() == null) && (skema1 == null) && (skema2 == null))
                binding.tvFoto.setVisibility(View.GONE);
            getPasien();
        }

        private void getPasien() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference dbPasien = db.collection("pasien").document(id_pasien);
            dbPasien.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    nama = documentSnapshot.getString("nama");
                    Log.d("FEEDBACK", "Berhasil Mengambil Data." + nama);
                }
                else Log.d("FEEDBACK", "Data Kosong.");
            }).addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Error: " +
                    e.toString(), Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onClick(View v) {
            Log.d("Menu Button", "onClick" + getAdapterPosition());
            FragmentActivity fragmentActivity = (FragmentActivity) (v.getContext());
            fragmentManager = fragmentActivity.getSupportFragmentManager();
            showPoupMenu(v);
        }

        private void showPoupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.edit_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        private void setScheme(String skema1, String skema2) {

            if (skema1 == null){
                binding.ivScheme1.setVisibility(View.GONE);
            }
            else {
                Picasso.get().load(skema1).fit().into(binding.ivScheme1);
                binding.ivScheme1.setVisibility(View.VISIBLE);
            }

            if (skema2 == null){
                binding.ivScheme2.setVisibility(View.GONE);
            }
            else {
                Picasso.get().load(skema2).fit().into(binding.ivScheme2);
                binding.ivScheme2.setVisibility(View.VISIBLE);
            }
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_data_menu:
                    Log.d("Menu", "onMenuItemClick: edit_data_menu");
                    Log.d("ID", "ID Pasien: " + id_pasien);
                    Log.d("ID", "ID Data: " + id_data);
                    DialogUpdateDataActivity dialog = new DialogUpdateDataActivity();
                    Bundle bundle = new Bundle();
                    bundle.putString(ID_DATA, id_data);
                    bundle.putString(ID_PASIEN, id_pasien);
                    bundle.putString(TANGGAL_DATA, tanggal_data);
                    dialog.setArguments(bundle);
                    dialog.show(fragmentManager, "Dialog Edit Data");
                    return true;
                case R.id.delete_data_menu:
                    Log.d("Menu", "onMenuItemClick: delete_data_menu");
                    DialogDeleteDataActiivity dialogDeleteDataActiivity = new DialogDeleteDataActiivity();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ID_DATA, id_data);
                    bundle1.putString(ID_PASIEN, id_pasien);
                    bundle1.putString(TANGGAL_DATA, tanggal_data);
                    dialogDeleteDataActiivity.setArguments(bundle1);
                    dialogDeleteDataActiivity.show(fragmentManager, "Dialog Delete Data");
                    return true;
                default:
                    return false;
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        public void setAdapter(ArrayList<String> listData) {
            FotoStreamAdapter fotoStreamAdapter = new FotoStreamAdapter(listData, nama, tanggal_data);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 4);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            binding.rvPhoto.setLayoutManager(gridLayoutManager);
            binding.rvPhoto.setAdapter(fotoStreamAdapter);
            fotoStreamAdapter.notifyDataSetChanged();
        }
    }
}
