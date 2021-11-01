package com.nomadev.direc.ui.home.dialogaddpasien;

import android.app.DatePickerDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.BuildConfig;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddPasienBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.dialogdeletedata.DialogDeleteDataActiivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class DialogUpdatePasienActivity extends DialogFragment {

    private ActivityDialogAddPasienBinding binding;
    private DatePickerDialog datePickerDialog;
    private FirebaseFirestore db;
    private int setSpinner;
    private String id;
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
        if (getArguments() != null) {
            String ID = "id";
            id = getArguments().getString(ID);
        }
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

        binding.etTitle.setText(getString(R.string.ubah_data_pasien));

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

        getPasienData();

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

            updateData(nama, kelamin, telepon, alamat, tanggal_lahir, kelaminInteger);
        });

        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.btnDelete.setOnClickListener(v -> {
            DialogDeleteDataActiivity dialog = new DialogDeleteDataActiivity();
            Bundle bundle = new Bundle();
            bundle.putString("id_pasien", id);
            bundle.putInt("type", 1);
            dialog.setArguments(bundle);
            if (getActivity()!=null){
                dialog.show(getActivity().getSupportFragmentManager(), "Dialog Delete Data");
            }
        });

        return view;
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String tanggalLahir = dayOfMonth + "/" + month + "/" + year;
            binding.btnTanggalLahir.setText(tanggalLahir);

            calculateAge(year, month, dayOfMonth);
        };
        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, dateSetListener, tahun, bulan, hari);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
    }

    private void updateData(String nama, String kelamin, String telepon, String alamat, String tanggalLahir, int kelaminInteger) {
        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbPasien = db.collection("pasien").document(id);

        // adding our data to our courses object class.
        PasienModel updatedPasienModel = new PasienModel(nama, kelaminInteger, telepon, alamat, tanggalLahir, false);

        // UPDATE TO "id" DOCUMENT
        dbPasien.update(
                "nama", updatedPasienModel.getNama(),
                "kelamin", updatedPasienModel.getKelamin(),
                "telepon", updatedPasienModel.getTelepon(),
                "alamat", updatedPasienModel.getAlamat(),
                "tanggalLahir", updatedPasienModel.getTanggalLahir()
        ).addOnSuccessListener(unused -> {
            Log.d("SUCCESS", "Data terkirim: " + nama + kelamin + telepon + alamat + tanggalLahir);
            Toast.makeText(getActivity(), "Data terkirim.", Toast.LENGTH_SHORT).show();
            updateAlgolia(nama, kelamin, telepon, alamat, tanggalLahir);
            if (getDialog() != null) {
                getDialog().dismiss();
            }
            if (getActivity() != null) {
                getActivity().recreate();
            }
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        });
    }

    private void getPasienData() {
        DocumentReference dbPasien = db.collection("pasien").document(id);

        dbPasien.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                nama = documentSnapshot.getString("nama");
                email = documentSnapshot.getString("email");
                tanggal_lahir = documentSnapshot.getString("tanggalLahir");
                telepon = documentSnapshot.getString("telepon");
                alamat = documentSnapshot.getString("alamat");
                kelaminInteger = Objects.requireNonNull(documentSnapshot.getLong("kelamin")).intValue();

                binding.etNamaLengkap.setText(nama);
                binding.etEmail.setText(email);
                binding.etNomorTelepon.setText(telepon);
                binding.etAlamat.setText(alamat);
                binding.btnTanggalLahir.setText(tanggal_lahir);
                binding.spinnerJenisKelamin.setSelection(kelaminInteger);

                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show());

    }

    private void calculateAge(int year, int month, int day) {
        // HITUNG USIA
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        String ageStr = String.valueOf(age);
        Log.d("usia", ageStr);
    }

    private void updateAlgolia(String nama, String kelamin, String telepon, String alamat, String tanggalLahir) {
        Client client = new Client(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_ADMIN_API_KEY);
        Index index = client.getIndex("pasien");

        List<JSONObject> array = new ArrayList<>();

        try {
            array.add(
                    new JSONObject()
                            .put("objectID", id)
                            .put("nama", nama)
                            .put("kelamin", kelamin)
                            .put("telepon", telepon)
                            .put("alamat", alamat)
                            .put("tanggalLahir", tanggalLahir)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        index.partialUpdateObjectsAsync(new JSONArray(array), null);
    }
}