package com.nomadev.direc.ui.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ItemHasilPeriksaPasienBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.dialogadddata.DialogUpdateDataActivity;
import com.nomadev.direc.ui.detail.dialogdeletedata.DialogDeleteDataActiivity;
import com.nomadev.direc.ui.home.dialogaddpasien.DialogUpdatePasienActivity;

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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private final ItemHasilPeriksaPasienBinding binding;
        private String id_pasien, id_data;
        private final String ID_PASIEN = "id_pasien";
        private final String ID_DATA = "id_data";
        private FragmentActivity fragmentActivity;
        private FragmentManager fragmentManager;

        public ViewHolder(@NonNull ItemHasilPeriksaPasienBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ibEditDown.setOnClickListener(this);
        }

        public void bind (HasilPeriksaModel hasilPeriksaModel){
            binding.tvHasilPeriksa.setText(hasilPeriksaModel.getHasil_periksa());
            binding.tvKeluhan.setText(hasilPeriksaModel.getKeluhan());
            binding.tvTerapi.setText(hasilPeriksaModel.getTerapi());
            id_pasien = hasilPeriksaModel.getId();
            id_data = hasilPeriksaModel.getId_data();
        }

        @Override
        public void onClick(View v) {
            Log.d("Menu Button", "onClick" + getAdapterPosition());
            fragmentActivity = (FragmentActivity)(v.getContext());
            fragmentManager = fragmentActivity.getSupportFragmentManager();
            showPoupMenu(v);
        }

        private void showPoupMenu(View v){
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.edit_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.edit_data_menu:
                    Log.d("Menu", "onMenuItemClick: edit_data_menu");
                    Log.d("ID", "ID Pasien: " + id_pasien);
                    Log.d("ID", "ID Data: " + id_data);
                    DialogUpdateDataActivity dialog = new DialogUpdateDataActivity();
                    Bundle bundle = new Bundle();
                    bundle.putString(ID_DATA, id_data);
                    bundle.putString(ID_PASIEN, id_pasien);
                    dialog.setArguments(bundle);
                    dialog.show(fragmentManager,"Dialog Edit Data");
                    return true;
                case R.id.delete_data_menu:
                    Log.d("Menu", "onMenuItemClick: delete_data_menu");
                    DialogDeleteDataActiivity dialogDeleteDataActiivity = new DialogDeleteDataActiivity();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ID_DATA, id_data);
                    bundle1.putString(ID_PASIEN, id_pasien);
                    dialogDeleteDataActiivity.setArguments(bundle1);
                    dialogDeleteDataActiivity.show(fragmentManager, "Dialog Delete Data");
                    return true;
                default:
                    return false;
            }
        }
    }
}
