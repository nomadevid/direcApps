package com.nomadev.direc.ui.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDetailBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.dialogadddata.DialogAddDataActivity;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private final String NAMA = "nama";
    private final String GENDER = "gender";
    private final String TELEPON = "telepon";
    private final String ALAMAT = "alamat";
    private final String TANGGAL_LAHIR = "tanggal_lahir";
    private final String ID = "id";

    private String nama, kelamin, telepon, alamat, tanggalLahir, id;

    private ActivityDetailBinding binding;
    private FirebaseFirestore db;
    private ArrayList<HasilPeriksaModel> hasilPeriksaModelArrayList;
    private HasilPeriksaAdapter hasilPeriksaAdapter;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nama = getIntent().getStringExtra(NAMA);
        kelamin = getIntent().getStringExtra(GENDER);
        telepon = getIntent().getStringExtra(TELEPON);
        alamat = getIntent().getStringExtra(ALAMAT);
        tanggalLahir = getIntent().getStringExtra(TANGGAL_LAHIR);
        id = getIntent().getStringExtra(ID);
        //Log.d("ID", "Ini ID : " + id);

        binding.tvDataDiri.setText(nama);
        binding.tvUsia.setText(tanggalLahir);
        binding.tvGender.setText(kelamin);
        binding.tvTelepon.setText(telepon);
        binding.tvAlamat.setText(alamat);

        //Recycle View Build Firebase
        hasilPeriksaModelArrayList = new ArrayList<>();
        hasilPeriksaAdapter = new HasilPeriksaAdapter(hasilPeriksaModelArrayList);
        db = FirebaseFirestore.getInstance();

        getHasilPeriksaData();
        showRecyclerView();

        binding.fabTambahData.setOnClickListener(v -> {
            DialogAddDataActivity dialog = new DialogAddDataActivity();
            Bundle bundle = new Bundle();
            bundle.putString(ID, id);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(),"Dialog Add Data");
        });
    }

    private void showRecyclerView() {
        binding.rvHasilPeriksa.setHasFixedSize(true);
        binding.rvHasilPeriksa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvHasilPeriksa.setAdapter(hasilPeriksaAdapter);
    }

    private void getHasilPeriksaData() {
        CollectionReference dbHasilPeriksa = db.collection("pasien").document(id).collection("history");
        dbHasilPeriksa.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : list) {
                    HasilPeriksaModel hasilPeriksaModel = documentSnapshot.toObject(HasilPeriksaModel.class);
                    hasilPeriksaModelArrayList.add(hasilPeriksaModel);
                }
                hasilPeriksaAdapter.notifyDataSetChanged();
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                Toast.makeText(getApplicationContext(), "Berhasil Mengambil Data.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
                Toast.makeText(getApplicationContext(), "Data Kosong.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show());
    }

//    private void showProgressBar(Boolean state) {
//        if (state) {
//            binding.progressBar.setVisibility(View.VISIBLE);
//        } else {
//            binding.progressBar.setVisibility(View.GONE);
//        }
//    }
}