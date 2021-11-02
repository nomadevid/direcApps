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
        dbRef.collection("history").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (int i = 0; i < queryDocumentSnapshots.size(); i++){
                id_data = queryDocumentSnapshots.getDocuments().get(i).getId();
                deleteDbData();
                deleteStorImage();
            }
        });

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
        deleteDbData();
        deleteStorImage();
        if (getDialog() != null) {
            getDialog().dismiss();
        }
        listener.RefreshLayout(true);
    }

    private void deleteDbData(){
        DocumentReference dbPasien = db.collection("pasien").document(id_pasien).collection("history").document(id_data);
        DocumentReference dbHistory = db.collection("history_pasien_all").document(id_data);

        dbPasien.delete();
        dbHistory.delete();
    }

    private void deleteStorImage(){
        StorageReference deleteFileScheme = FirebaseStorage.getInstance().getReference().child(id_data).child("scheme");
        StorageReference deleteFileImage = FirebaseStorage.getInstance().getReference().child(id_data);

        deleteFileScheme.listAll().addOnSuccessListener(listResult -> {
            for (int i = 0; i< listResult.getItems().size(); i++){
                listResult.getItems().get(i).delete();
            }
            Log.d("SUCCESS", "onSuccess: Data Storage Dihapus");
        }).addOnFailureListener(e -> Log.e("FAIL", "deleteData: ", e));

        deleteFileImage.listAll().addOnSuccessListener(listResult -> {
            for (int i = 0; i< listResult.getItems().size(); i++){
                listResult.getItems().get(i).delete();
            }
            Log.d("SUCCESS", "onSuccess: Data Storage Dihapus");
        }).addOnFailureListener(e -> Log.e("FAIL", "deleteData: ", e));
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