package com.nomadev.direc.ui.detail.dialogdeletedata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogDeleteDataActiivityBinding;
import com.nomadev.direc.databinding.ActivityDialogUpdateDataBinding;

public class DialogDeleteDataActiivity extends DialogFragment {

    private ActivityDialogDeleteDataActiivityBinding binding;
    private FirebaseFirestore db;
    private final String ID_PASIEN = "id_pasien";
    private final String ID_DATA = "id_data";
    private String id_data, id_pasien;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogDeleteDataActiivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        id_data = getArguments().getString(ID_DATA);
        id_pasien = getArguments().getString(ID_PASIEN);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        //int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_delete_data_actiivity);
        getDialog().show();

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
                if (task.isComplete()){
                    Toast.makeText(getActivity(), "Data telah dihapus", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    getActivity().recreate();
                } else {
                    Toast.makeText(getActivity(), "Data gagal dihapus", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            }
        });
    }
}