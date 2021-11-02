package com.nomadev.direc.ui.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements
        DialogAddDataActivity.DialogAddDataListener,
        DialogUpdateDataActivity.DialogUpdateDataListener,
        DialogDeleteDataActiivity.DialogDeleteDataListener,
        DialogExport.DialogExportListener {

    public static final String NAMA = "nama";
    public static final String GENDER = "gender";
    public static final String TELEPON = "telepon";
    public static final String ALAMAT = "alamat";
    public static final String TANGGAL_LAHIR = "tanggal_lahir";
    public static final String ID = "id";

    private final int GENDER_LAKI = 0;

    private String nama, telepon, alamat, tanggalLahir, id, email;
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

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        id = getIntent().getStringExtra(ID);

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

        binding.btnSaveCsv.setOnClickListener(v -> {
            if (hasilPeriksaModelArrayList.size() > 0) {
                DialogExport dialog = new DialogExport();
                dialog.show(getSupportFragmentManager(), "DialogExport");
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.riwayat_kosong), Toast.LENGTH_LONG).show();
            }
        });
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

    private void createFile(List<HasilPeriksaModel> list) {
        Workbook wb = new HSSFWorkbook();

        Cell cell;

        Sheet sheet;
        sheet = wb.createSheet("Direc_" + nama);

        //Now column and row
        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("ID");

        cell = row.createCell(1);
        cell.setCellValue("TANGGAL");

        cell = row.createCell(2);
        cell.setCellValue("NAMA PASIEN");

        cell = row.createCell(3);
        cell.setCellValue("EMAIL");

        cell = row.createCell(4);
        cell.setCellValue("NO. TELEPON");

        cell = row.createCell(5);
        cell.setCellValue("ALAMAT");

        cell = row.createCell(6);
        cell.setCellValue("PEMERIKSA");

        cell = row.createCell(7);
        cell.setCellValue("PENYAKIT");

        cell = row.createCell(8);
        cell.setCellValue("KELUHAN");

        cell = row.createCell(9);
        cell.setCellValue("HASIL PERIKSA");

        cell = row.createCell(10);
        cell.setCellValue("TERAPI");

        cell = row.createCell(11);
        cell.setCellValue("TAGIHAN");

        //column width
        sheet.setColumnWidth(0, (10 * 200));
        sheet.setColumnWidth(1, (30 * 200));
        sheet.setColumnWidth(2, (30 * 200));
        sheet.setColumnWidth(3, (30 * 200));
        sheet.setColumnWidth(4, (30 * 200));
        sheet.setColumnWidth(5, (30 * 200));
        sheet.setColumnWidth(6, (60 * 200));
        sheet.setColumnWidth(7, (20 * 200));
        sheet.setColumnWidth(8, (30 * 200));
        sheet.setColumnWidth(9, (30 * 200));
        sheet.setColumnWidth(10, (30 * 200));
        sheet.setColumnWidth(11, (20 * 200));

        for (int i = 0; i < list.size(); i++) {
            Row row1 = sheet.createRow(i + 1);

            cell = row1.createCell(0);
            cell.setCellValue(list.get(i).getIdData());

            cell = row1.createCell(1);
            cell.setCellValue(list.get(i).getTanggal());

            cell = row1.createCell(2);
            cell.setCellValue(nama);

            cell = row1.createCell(3);
            cell.setCellValue(email);

            cell = row1.createCell(4);
            cell.setCellValue(telepon);

            cell = row1.createCell(5);
            cell.setCellValue(alamat);

            cell = row1.createCell(6);
            cell.setCellValue(list.get(i).getPemeriksa());

            cell = row1.createCell(7);
            cell.setCellValue(list.get(i).getPenyakit());

            cell = row1.createCell(8);
            cell.setCellValue(list.get(i).getKeluhan());

            cell = row1.createCell(9);
            cell.setCellValue(list.get(i).getHasil_periksa());

            cell = row1.createCell(10);
            cell.setCellValue(list.get(i).getTerapi());

            cell = row1.createCell(11);
            cell.setCellValue(list.get(i).getTagihan());

            sheet.setColumnWidth(0, (10 * 200));
            sheet.setColumnWidth(1, (30 * 200));
            sheet.setColumnWidth(2, (30 * 200));
            sheet.setColumnWidth(3, (30 * 200));
            sheet.setColumnWidth(4, (30 * 200));
            sheet.setColumnWidth(5, (30 * 200));
            sheet.setColumnWidth(6, (60 * 200));
            sheet.setColumnWidth(7, (20 * 200));
            sheet.setColumnWidth(8, (30 * 200));
            sheet.setColumnWidth(9, (30 * 200));
            sheet.setColumnWidth(10, (30 * 200));
            sheet.setColumnWidth(11, (20 * 200));
        }

        String folderName = "Direc";
        String fileName = folderName + "_" + nama + "_" + System.currentTimeMillis() + ".xls";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + folderName + File.separator + fileName;

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + File.separator + folderName);
        if (!file.exists()) {
            file.mkdirs();
            Toast.makeText(getApplicationContext(), "FOLDER CREATED", Toast.LENGTH_LONG).show();
        }

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(path);
            wb.write(outputStream);
            Toast.makeText(getApplicationContext(), "Rekam Data exported in " + path, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Failed: " + e.toString(), Toast.LENGTH_LONG).show();
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
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
                email = documentSnapshot.getString("email");

                binding.tvDataDiri.setText(nama);
                binding.tvUsia.setText(getString(R.string.usia_terisi, calculateAge(tanggalLahir)));
                if (kelamin == GENDER_LAKI) {
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
                listSection.add(new HasilPeriksaModel("", "", "", "", "", header, "", 0, "", "", null, true));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createFile(hasilPeriksaModelArrayList);
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onExport(Boolean state) {
        if (state) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 1);
                } else {
                    createFile(hasilPeriksaModelArrayList);
                }
            } else {
                createFile(hasilPeriksaModelArrayList);
            }
        }
    }
}