package com.nomadev.direc.ui.detail.dialogdeletedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogDeleteDataActiivityBinding;
import com.nomadev.direc.databinding.ActivityDialogUpdateDataBinding;
import com.nomadev.direc.ui.detail.dialogadddata.DialogUpdateDataActivity;

public class DialogDeleteDataActiivity extends DialogFragment {

    private ActivityDialogDeleteDataActiivityBinding binding;
    private FirebaseFirestore db;
    private final String ID_PASIEN = "id_pasien";
    private final String ID_DATA = "id_data";
    private final String TANGGAL_DATA = "tanggal_data";
    private String id_data, id_pasien, tanggal_data;
    private DialogDeleteDataListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogDeleteDataActiivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        id_data = getArguments().getString(ID_DATA);
        id_pasien = getArguments().getString(ID_PASIEN);
        tanggal_data = getArguments().getString(TANGGAL_DATA);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        //int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_delete_data_actiivity);
        getDialog().show();
        showProgressBar(false);

        binding.btnHapus.setOnClickListener(v -> {
            deleteData();
        });

        binding.btnTidak.setOnClickListener(v -> {
            getDialog().dismiss();
        });

        return view;
    }

    private void deleteData() {
        DocumentReference dbPasien = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        dbPasien.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
//                    Toast.makeText(getActivity(), "Data telah dihapus", Toast.LENGTH_SHORT).show();
                    deleteHistory();
//                    getDialog().dismiss();
//                    getActivity().recreate();
                } else {
//                    Toast.makeText(getActivity(), "Data gagal dihapus", Toast.LENGTH_SHORT).show();
//                    getDialog().dismiss();
                }
            }
        });

        StorageReference deleteFileImage = FirebaseStorage.getInstance().getReference(id_data);

        deleteFileImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("SUCCESS", "onSuccess: Data Storage Dihapus");
            }
        }).addOnFailureListener(e -> Log.e("FAIL", "deleteData: ", e));

        getDialog().dismiss();
        listener.RefreshLayout(true);
        showProgressBar(false);
    }

    private void deleteHistory() {
        DocumentReference dbHistory = db.collection("history_pasien").document(tanggal_data).collection(tanggal_data).document(id_data);

        dbHistory.delete().addOnCompleteListener(task -> {
            if (task.isComplete()) {
                Log.d("deleteHistory", "Data terhapus.");
            } else {
                Log.d("deleteHistory", "Gagal.");
            }
        });
    }

    private void showProgressBar(Boolean state) {
        if (state) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DialogDeleteDataListener) context;
    }

    public interface DialogDeleteDataListener{
        void RefreshLayout(Boolean state);
    }
}