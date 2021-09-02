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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDetailBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.dialogadddata.DialogAddDataActivity;
import com.nomadev.direc.ui.home.dialogaddpasien.DialogUpdatePasienActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    public static final String NAMA = "nama";
    public static final String GENDER = "gender";
    public static final String TELEPON = "telepon";
    public static final String ALAMAT = "alamat";
    public static final String TANGGAL_LAHIR = "tanggal_lahir";
    public static final String ID = "id";

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

        id = getIntent().getStringExtra(ID);
        //Log.d("ID", "Ini ID : " + id);


        //Recycle View Build Firebase
        hasilPeriksaModelArrayList = new ArrayList<>();
        hasilPeriksaAdapter = new HasilPeriksaAdapter(hasilPeriksaModelArrayList);
        db = FirebaseFirestore.getInstance();

        getPasienData();
        getHasilPeriksaData();
        showRecyclerView();

        binding.fabTambahData.setOnClickListener(v -> {
            DialogAddDataActivity dialog = new DialogAddDataActivity();
            Bundle bundle = new Bundle();
            bundle.putString(ID, id);
            bundle.putString(NAMA, nama);
            bundle.putString(TANGGAL_LAHIR, tanggalLahir);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(),"Dialog Add Data");
        });

        binding.ibEdit.setOnClickListener(v -> {
            DialogUpdatePasienActivity dialog = new DialogUpdatePasienActivity();
            Bundle bundle = new Bundle();
            bundle.putString(ID, id);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(),"Dialog Edit Pasien");
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
                    hasilPeriksaModel.setId_data(documentSnapshot.getId());
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

    private void getPasienData() {
        DocumentReference dbPasien = db.collection("pasien").document(id);

        dbPasien.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    nama = documentSnapshot.getString("nama");
                    tanggalLahir = documentSnapshot.getString("tanggalLahir");
                    kelamin = documentSnapshot.getString("kelamin");
                    telepon = documentSnapshot.getString("telepon");
                    alamat = documentSnapshot.getString("alamat");

                    binding.tvDataDiri.setText(nama);
                    binding.tvUsia.setText(getString(R.string.usia_terisi, calculateAge(tanggalLahir)));
                    binding.tvGender.setText(kelamin);
                    binding.tvTelepon.setText(telepon);
                    binding.tvAlamat.setText(alamat);

                    Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                }
                else {
                    Log.d("FEEDBACK", "Data Kosong.");
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show());

    }

    private String calculateAge(String tanggalLahir) {
        String ageString = "";
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
        return ageString;
    }
}