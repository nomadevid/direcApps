package com.nomadev.direc.ui.home.dialogaddpasien;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddPasienBinding;
import com.nomadev.direc.ui.detail.DetailActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DialogAddPasienActivity extends DialogFragment {

    private ActivityDialogAddPasienBinding binding;
    private DatePickerDialog datePickerDialog;
    private FirebaseFirestore db;
    private String nama;
    private String kelamin;
    private String telepon;
    private String alamat;
    private String tanggal_lahir;
    private String email;
    private int kelaminInteger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogAddPasienBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        initDatePicker();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);

        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setContentView(R.layout.activity_dialog_add_pasien);
            getDialog().show();
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        getDialog().getWindow().setAttributes(layoutParams);

        String[] gender = {getString(R.string.gender_laki), getString(R.string.gender_perempuan)};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_text_style, gender);
        binding.spinnerJenisKelamin.setAdapter(arrayAdapter);

        binding.btnTanggalLahir.setFocusable(true);
        binding.btnTanggalLahir.setFocusableInTouchMode(true);
        binding.btnTanggalLahir.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                datePickerDialog.show();
            }
        });

        binding.btnTanggalLahir.setOnClickListener(v -> datePickerDialog.show());

        // BUTTON SIMPAN
        binding.btnSimpan.setOnClickListener(v -> {
            kelaminInteger = binding.spinnerJenisKelamin.getSelectedItemPosition();
            nama = String.valueOf(binding.etNamaLengkap.getText());
            kelamin = String.valueOf(kelaminInteger);
            telepon = String.valueOf(binding.etNomorTelepon.getText());
            alamat = String.valueOf(binding.etAlamat.getText());
            tanggal_lahir = String.valueOf(binding.btnTanggalLahir.getText());
            email = String.valueOf(binding.etEmail.getText());

            if (TextUtils.isEmpty(nama)) {
                binding.etNamaLengkap.setError(getString(R.string.masukkan_nama_lengkap));
                return;
            }
            if (TextUtils.isEmpty(tanggal_lahir)) {
                binding.btnTanggalLahir.setError(getString(R.string.masukkan_tanggal_lahir));
                return;
            }
            if (TextUtils.isEmpty(telepon)) {
                binding.etNomorTelepon.setError(getString(R.string.masukkan_nomor_telepon));
                return;
            }
            if (TextUtils.isEmpty(alamat)) {
                binding.etAlamat.setError(getString(R.string.masukkan_alamat));
                return;
            }
            if (TextUtils.isEmpty(email)) {
                binding.etEmail.setError(getString(R.string.masukkan_email));
                return;
            }

            postDataWithId(nama, kelamin, telepon, alamat, tanggal_lahir, email, kelaminInteger);
        });

        return view;
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String yearString = String.valueOf(year);
            String monthString = String.valueOf(month);
            String dayOfMonthString = String.valueOf(dayOfMonth);
            binding.btnTanggalLahir.setText(String.format("%s/%s/%s", dayOfMonthString, monthString, yearString));
        };
        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, dateSetListener, tahun, bulan, hari);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
    }

    private void postDataWithId(String nama, String kelamin, String telepon, String alamat, String tanggalLahir, String email, int kelaminInteger) {
        DocumentReference dbPasien = db.collection("pasien").document();
        String id = dbPasien.getId();
        DocumentReference dbData = db.collection("pasien").document(id);

        Map<Object, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("nama", nama);
        map.put("kelamin", kelaminInteger);
        map.put("telepon", telepon);
        map.put("alamat", alamat);
        map.put("tanggalLahir", tanggalLahir);
        map.put("email", email);

        dbData.set(map).addOnSuccessListener(unused -> {
            Log.d("id", id);
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
        });

        Objects.requireNonNull(getDialog()).dismiss();
        intentToDetail(nama, kelamin, telepon, alamat, tanggalLahir, id);
    }

    private void intentToDetail(String nama, String kelamin, String telepon, String alamat, String tanggalLahir, String id) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(DetailActivity.NAMA, nama);
        intent.putExtra(DetailActivity.GENDER, kelamin);
        intent.putExtra(DetailActivity.TELEPON, telepon);
        intent.putExtra(DetailActivity.ALAMAT, alamat);
        intent.putExtra(DetailActivity.TANGGAL_LAHIR, tanggalLahir);
        intent.putExtra(DetailActivity.ID, id);
        startActivity(intent);
    }
}