package com.nomadev.direc.ui.home.dialogaddpasien;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogUpdatePasienBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.home.HomeActivity;
import com.nomadev.direc.ui.home.byname.ByNameFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DialogUpdatePasienActivity extends DialogFragment {

    private ActivityDialogUpdatePasienBinding binding;
    private DatePickerDialog datePickerDialog;
    private FirebaseFirestore db;
    private final String ID = "id";
    int setSpinner;
    private String id;
    private String nama;
    private String kelamin;
    private String telepon;
    private String alamat;
    private String tanggal_lahir;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogUpdatePasienBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        id = getArguments().getString(ID);
        initDatePicker();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_add_pasien);
        getDialog().show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        //layoutParams.height = height;
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

        binding.btnTanggalLahir.setOnClickListener(v -> {
            datePickerDialog.show();
        });

        getPasienData();

        // BUTTON SIMPAN
        binding.btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            nama = String.valueOf(binding.etNamaLengkap.getText());
            kelamin = String.valueOf(binding.spinnerJenisKelamin.getSelectedItem());
            telepon = String.valueOf(binding.etNomorTelepon.getText());
            alamat = String.valueOf(binding.etAlamat.getText());
            tanggal_lahir = String.valueOf(binding.btnTanggalLahir.getText());

            updateData(nama, kelamin, telepon, alamat, tanggal_lahir);
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

//    private void postData(String nama, String kelamin, String telepon, String alamat, String tanggalLahir) {
//        // creating a collection reference
//        // for our Firebase Firetore database.
//        CollectionReference dbPasien = db.collection("pasien");
//
//        // adding our data to our courses object class.
//        PasienModel pasienModel = new PasienModel(nama, kelamin, telepon, alamat, tanggalLahir);
//
//        // POST TO "pasien" COLLECTION
//        dbPasien.add(pasienModel).addOnSuccessListener(documentReference -> {
//            Log.d("SUCCESS", "Data terkirim: " + nama + kelamin + telepon + alamat + tanggalLahir);
//            Toast.makeText(getActivity(), "Data terkirim.", Toast.LENGTH_SHORT).show();
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .setReorderingAllowed(true)
//                    .replace(R.id.fragment_home, ByNameFragment.class, null)
//                    .commit();
//            getDialog().dismiss();
//        }).addOnFailureListener(e -> {
//            Log.d("GAGAL", "Error: " + e.toString());
//            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
//            getDialog().dismiss();
//        });
//    }

    private void updateData(String nama, String kelamin, String telepon, String alamat, String tanggalLahir) {
        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbPasien = db.collection("pasien").document(id);

        // adding our data to our courses object class.
        PasienModel updatedPasienModel = new PasienModel(nama, kelamin, telepon, alamat, tanggalLahir, false);

        // UPDATE TO "id" DOCUMENT
        dbPasien.update(
                "nama", updatedPasienModel.getNama(),
                "kelamin", updatedPasienModel.getKelamin(),
                "telepon", updatedPasienModel.getTelepon(),
                "alamat", updatedPasienModel.getAlamat(),
                "tanggalLahir", updatedPasienModel.getTanggalLahir()
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("SUCCESS", "Data terkirim: " + nama + kelamin + telepon + alamat + tanggalLahir);
                Toast.makeText(getActivity(), "Data terkirim.", Toast.LENGTH_SHORT).show();
                updateAlgolia(nama, kelamin, telepon, alamat, tanggalLahir);
                getDialog().dismiss();
                getActivity().recreate();
            }
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });
    }

    private void getPasienData() {
        DocumentReference dbPasien = db.collection("pasien").document(id);

        dbPasien.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    nama = documentSnapshot.getString("nama");
                    tanggal_lahir = documentSnapshot.getString("tanggalLahir");
                    telepon = documentSnapshot.getString("telepon");
                    alamat = documentSnapshot.getString("alamat");
                    kelamin = documentSnapshot.getString("kelamin");
                    if (kelamin.equals("Laki - laki")) setSpinner = 0;
                    else setSpinner = 1;

                    binding.etNamaLengkap.setText(nama);
                    binding.etNomorTelepon.setText(telepon);
                    binding.etAlamat.setText(alamat);
                    binding.btnTanggalLahir.setText(tanggal_lahir);
                    binding.spinnerJenisKelamin.setSelection(setSpinner);

                    Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                } else {
                    Log.d("FEEDBACK", "Data Kosong.");
                }
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
        String appid = "HLDBOC7XRI";
        String adminApiKey = "1a40eab368fd30c1ce3333a8e4658ca0";

        Client client = new Client(appid, adminApiKey);
        Index index = client.getIndex("pasien");

        List<JSONObject> array = new ArrayList<JSONObject>();

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