package com.nomadev.direc.ui.detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDetailBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.ui.detail.dialogadddata.DialogAddDataActivity;
import com.nomadev.direc.ui.detail.dialogadddata.DialogUpdateDataActivity;
import com.nomadev.direc.ui.detail.dialogdeletedata.DialogDeleteDataActiivity;
import com.nomadev.direc.ui.home.dialogaddpasien.DialogUpdatePasienActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements DialogAddDataActivity.DialogAddDataListener, DialogUpdateDataActivity.DialogUpdateDataListener, DialogDeleteDataActiivity.DialogDeleteDataListener {

    public static final String NAMA = "nama";
    public static final String GENDER = "gender";
    public static final String TELEPON = "telepon";
    public static final String ALAMAT = "alamat";
    public static final String TANGGAL_LAHIR = "tanggal_lahir";
    public static final String ID = "id";

    private final int GENDER_LAKI = 0;

    private String nama, telepon, alamat, tanggalLahir, id;
    private int kelamin;

    private ActivityDetailBinding binding;
    private FirebaseFirestore db;
    private ArrayList<HasilPeriksaModel> hasilPeriksaModelArrayList;
    private ArrayList<HasilPeriksaModel> listSection;
    private HasilPeriksaAdapter hasilPeriksaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = getIntent().getStringExtra(ID);

        //Recycle View Build Firebase
        hasilPeriksaModelArrayList = new ArrayList<>();
        listSection = new ArrayList<>();
        hasilPeriksaAdapter = new HasilPeriksaAdapter(listSection);
        db = FirebaseFirestore.getInstance();

        showProgressBar(true);
        getPasienData();
        getHasilPeriksaData();
        showRecyclerView();

        binding.refreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            getPasienData();
            getHasilPeriksaData();
            showRecyclerView();
        }, 2000));

        binding.fabTambahData.setOnClickListener(v -> {
            DialogAddDataActivity dialog = new DialogAddDataActivity();
            Bundle bundle = new Bundle();
            bundle.putString(ID, id);
            bundle.putString(NAMA, nama);
            bundle.putString(TANGGAL_LAHIR, tanggalLahir);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "Dialog Add Data");
        });

        binding.ibEdit.setOnClickListener(v -> {
            DialogUpdatePasienActivity dialog = new DialogUpdatePasienActivity();
            Bundle bundle = new Bundle();
            bundle.putString(ID, id);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "Dialog Edit Pasien");
            dialog.getShowsDialog();
            Log.d("DIALOG", "DIALOG EDIT :  " + dialog.getShowsDialog());
        });

        binding.ibBack.setOnClickListener(v -> onBackPressed());
    }

    private void showRecyclerView() {
        binding.rvHasilPeriksa.setVisibility(View.VISIBLE);
        binding.rvHasilPeriksa.setHasFixedSize(true);
        binding.rvHasilPeriksa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvHasilPeriksa.setAdapter(hasilPeriksaAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getHasilPeriksaData() {
        hasilPeriksaModelArrayList.clear();
        CollectionReference dbHasilPeriksa = db.collection("pasien").document(id).collection("history");
        Query query = dbHasilPeriksa.orderBy("timeStamp", Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            showProgressBar(false);
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot documentSnapshot : list) {
                    HasilPeriksaModel hasilPeriksaModel = documentSnapshot.toObject(HasilPeriksaModel.class);
                    if (hasilPeriksaModel != null) {
                        hasilPeriksaModel.setId_data(documentSnapshot.getId());
                        hasilPeriksaModel.setUrlString((ArrayList<String>) documentSnapshot.get("foto"));
                        hasilPeriksaModelArrayList.add(hasilPeriksaModel);
                    }
                }
                showInfo(false);
                showRecyclerView();
                getHeaderList(hasilPeriksaModelArrayList);
                hasilPeriksaAdapter.notifyDataSetChanged();
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
            } else {
                showInfo(true);
                binding.rvHasilPeriksa.setVisibility(View.GONE);
                Log.d("FEEDBACK", "Data Kosong.");
            }
        }).addOnFailureListener(e -> {
            showProgressBar(false);
            Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        });
        binding.refreshLayout.setRefreshing(false);
    }

    private void getPasienData() {
        DocumentReference dbPasien = db.collection("pasien").document(id);

        dbPasien.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                nama = documentSnapshot.getString("nama");
                tanggalLahir = documentSnapshot.getString("tanggalLahir");
                kelamin = Objects.requireNonNull(documentSnapshot.getLong("kelamin")).intValue();
                telepon = documentSnapshot.getString("telepon");
                alamat = documentSnapshot.getString("alamat");

                binding.tvDataDiri.setText(nama);
                binding.tvUsia.setText(getString(R.string.usia_terisi, calculateAge(tanggalLahir)));
                if (kelamin == GENDER_LAKI){
                    binding.tvGender.setText(getString(R.string.gender_laki));
                } else {
                    binding.tvGender.setText(getString(R.string.gender_perempuan));
                }
                binding.tvTelepon.setText(telepon);
                binding.tvAlamat.setText(alamat);

                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show());

    }

    private void getHeaderList(ArrayList<HasilPeriksaModel> list) {

        String lastHeader = "";
        int size = list.size();
        listSection.clear();

        for (int i = 0; i < size; i++) {
            HasilPeriksaModel user = list.get(i);
            Log.d("getHeader", user.getTanggal());
            String header = String.valueOf(user.getTanggal());

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                listSection.add(new HasilPeriksaModel("","", "", "", header, "", 0, null, true));
            }
            listSection.add(user);
        }
        Log.d("getHeader", listSection.toString());
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

    private void showProgressBar(Boolean state) {
        if (state) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void showInfo(Boolean state) {
        if (state) {
            binding.tvKeterangan.setVisibility(View.VISIBLE);
        } else {
            binding.tvKeterangan.setVisibility(View.GONE);
        }
    }

    @Override
    public void RefreshLayout(Boolean state) {
        if (state) getHasilPeriksaData();
    }
}