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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddPasienBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.home.HomeActivity;
import com.nomadev.direc.ui.home.byname.ByNameFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DialogAddPasienActivity extends DialogFragment {

    private ActivityDialogAddPasienBinding binding;
    private DatePickerDialog datePickerDialog;
    private FirebaseFirestore db;
    private final ArrayList<PasienModel> listPasien = new ArrayList<>();
    private String nama;
    private String kelamin;
    private String telepon;
    private String alamat;
    private String tanggal_lahir;
    private String id;
    private String formattedDate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogAddPasienBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
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

        // BUTTON SIMPAN
        binding.btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            nama = String.valueOf(binding.etNamaLengkap.getText());
            kelamin = String.valueOf(binding.spinnerJenisKelamin.getSelectedItem());
            telepon = String.valueOf(binding.etNomorTelepon.getText());
            alamat = String.valueOf(binding.etAlamat.getText());
            tanggal_lahir = String.valueOf(binding.btnTanggalLahir.getText());

//            postData(nama, kelamin, telepon, alamat, tanggal_lahir);
            postDataWithId(nama, kelamin, telepon, alamat, tanggal_lahir);
//            updateAlgolia(nama);
        });

        return view;
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            binding.btnTanggalLahir.setText(String.valueOf(dayOfMonth + "/" + month + "/" + year));
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

    private void postDataWithId(String nama, String kelamin, String telepon, String alamat, String tanggalLahir) {
        DocumentReference dbPasien = db.collection("pasien").document();
        String id = dbPasien.getId();
        DocumentReference dbData = db.collection("pasien").document(id);

        Map map = new HashMap<>();
        map.put("id", id);
        map.put("nama", nama);
        map.put("kelamin", kelamin);
        map.put("telepon", telepon);
        map.put("alamat", alamat);
        map.put("tanggalLahir", tanggalLahir);

        dbData.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("id", id);
                postAlgolia(nama, kelamin, telepon, alamat, tanggal_lahir, id);
                getDialog().dismiss();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByNameFragment.class, null)
                        .commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("GAGAL", "Error: " + e.toString());
                getDialog().dismiss();
            }
        });
    }

    private void postAlgolia(String nama, String kelamin, String telepon, String alamat, String tanggalLahir, String id) {
        String appid = "HLDBOC7XRI";
        String adminApiKey = "1a40eab368fd30c1ce3333a8e4658ca0";

        Client client = new Client(appid, adminApiKey);
        Index index = client.getIndex("pasien");

        ArrayList<JSONObject> array = new ArrayList<JSONObject>();

        try {
            array.add(
                    new JSONObject()
                            .put("objectID", id)
                            .put("nama", nama)
                            .put("kelamin", kelamin)
                            .put("tanggalLahir", tanggalLahir)
                            .put("telepon", telepon)
                            .put("alamat", alamat)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        index.addObjectsAsync(new JSONArray(array), null);
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
}