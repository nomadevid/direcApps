package com.nomadev.direc.ui.detail.dialogadddata;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogUpdateDataBinding;
import com.nomadev.direc.databinding.ActivityDialogUpdatePasienBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.PasienModel;

public class DialogUpdateDataActivity extends DialogFragment {

    private ActivityDialogUpdateDataBinding binding;
    private FirebaseFirestore db;
    private final String ID_PASIEN = "id_pasien";
    private final String ID_DATA = "id_data";
    private String id_data, id_pasien, keluhan, hasil_periksa, terapi, tanggal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogUpdateDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        id_data = getArguments().getString(ID_DATA);
        id_pasien = getArguments().getString(ID_PASIEN);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_update_data);
        getDialog().show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = height;
        getDialog().getWindow().setAttributes(layoutParams);

        getData();

        //Simpan
        binding.btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            keluhan = String.valueOf(binding.etKeluhan.getText());
            hasil_periksa = String.valueOf(binding.etHasilPeriksa.getText());
            terapi = String.valueOf(binding.etTerapi.getText());

            updateData(hasil_periksa, keluhan, terapi);
        });

        return view;
    }

    private void getData() {
        DocumentReference dbPasien = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        dbPasien.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    keluhan = documentSnapshot.getString("keluhan");
                    hasil_periksa = documentSnapshot.getString("hasil_periksa");
                    terapi = documentSnapshot.getString("terapi");
                    tanggal = documentSnapshot.getString("tanggal");

                    binding.etKeluhan.setText(keluhan);
                    binding.etHasilPeriksa.setText(hasil_periksa);
                    binding.etTerapi.setText(terapi);

                    Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                } else {
                    Log.d("FEEDBACK", "Data Kosong.");
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show());

    }

    private void updateData(String hasil_periksa, String keluhan, String terapi) {
        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        // adding our data to our courses object class.
        HasilPeriksaModel updatedDataModel = new HasilPeriksaModel(hasil_periksa, keluhan, tanggal, terapi);

        // UPDATE TO "id" DOCUMENT
        dbData.update(
                "hasil_periksa", updatedDataModel.getHasil_periksa(),
                "keluhan", updatedDataModel.getKeluhan(),
                "tanggal", updatedDataModel.getTanggal(),
                "terapi", updatedDataModel.getTerapi()
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("SUCCESS", "Data terkirim: " + hasil_periksa + keluhan + tanggal + terapi);
                Toast.makeText(getActivity(), "Data terkirim.", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
            }
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });
    }
}