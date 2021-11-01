package com.nomadev.direc.ui.detail.dialogdeletedata;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogDeleteDataActiivityBinding;
import com.nomadev.direc.ui.detail.HasilPeriksaAdapter;

public class DialogDeleteDataActiivity extends DialogFragment {

    private ActivityDialogDeleteDataActiivityBinding binding;
    private FirebaseFirestore db;
    private String id_data, id_pasien, tanggal_data;
    private int type;
    private DialogDeleteDataListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogDeleteDataActiivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            id_data = getArguments().getString(HasilPeriksaAdapter.ViewHolder.ID_DATA);
            id_pasien = getArguments().getString(HasilPeriksaAdapter.ViewHolder.ID_PASIEN);
            tanggal_data = getArguments().getString(HasilPeriksaAdapter.ViewHolder.TANGGAL_DATA);
            type = getArguments().getInt("type", 0);
        }

        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setContentView(R.layout.activity_dialog_delete_data_actiivity);
            getDialog().show();
        }

        if (type == 0) {
            binding.btnHapus.setOnClickListener(v -> deleteData());
        } else {
            binding.btnHapus.setOnClickListener(v -> deletePasien());
        }

        binding.btnTidak.setOnClickListener(v -> getDialog().dismiss());

        return view;
    }

    private void deletePasien() {
        DocumentReference dbRef = db.collection("pasien").document(id_pasien);
        dbRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SUCCESS", "onSuccess: Dihapus");
            }

            if (getDialog() != null) {
                getDialog().dismiss();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }

    private void deleteData() {
        DocumentReference dbPasien = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        dbPasien.delete().addOnCompleteListener(task -> {
            if (task.isComplete()) deleteHistory();
        });

        StorageReference deleteFileImage = FirebaseStorage.getInstance().getReference().child(id_data);

        deleteFileImage.delete().addOnSuccessListener(unused -> Log.d("SUCCESS", "onSuccess: Data Storage Dihapus"))
                .addOnFailureListener(e -> Log.e("FAIL", "deleteData: ", e));

        if (getDialog() != null) {
            getDialog().dismiss();
        }
        listener.RefreshLayout(true);
    }

    private void deleteHistory() {
        DocumentReference dbHistory = db.collection("history_pasien_all").document(id_data);

        dbHistory.delete().addOnCompleteListener(task -> {
            if (task.isComplete()) {
                Log.d("deleteHistory", "Data terhapus.");
            } else {
                Log.d("deleteHistory", "Gagal.");
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DialogDeleteDataListener) context;
    }

    public interface DialogDeleteDataListener {
        void RefreshLayout(Boolean state);
    }
}