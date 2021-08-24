package com.nomadev.direc.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityHomeBinding;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.home.byage.ByAgeFragment;
import com.nomadev.direc.ui.home.bycalendar.ByCalendarFragment;
import com.nomadev.direc.ui.home.byname.ByNameFragment;
import com.nomadev.direc.ui.home.dialogaddpasien.DialogAddPasienActivity;
import com.nomadev.direc.ui.search.SearchActivity;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private FirebaseFirestore db;
    private String nama;
    private String kelamin;
    private String telepon;
    private String alamat;
    private String tanggal_lahir;
    private String usia;
    private DatePickerDialog datePickerDialog;
    private Button btnTanggalLahir;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_home, ByNameFragment.class, null)
                    .commit();
        }

        binding.ibFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(HomeActivity.this, v);
            popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
            popup.inflate(R.menu.filter_menu);
            popup.show();
        });

        binding.ibSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        binding.floatingActionButton.setOnClickListener(v -> {
            DialogAddPasienActivity dialog = new DialogAddPasienActivity();
            dialog.show(getSupportFragmentManager(), "DialogAddPasien");
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date:
                Toast.makeText(this, "date selected", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByCalendarFragment.class, null)
                        .commit();
                return true;
            case R.id.nama:
                Toast.makeText(this, "nama selected", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByNameFragment.class, null)
                        .commit();
                return true;
            case R.id.usia:
                Toast.makeText(this, "usia selected", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByAgeFragment.class, null)
                        .commit();
                return true;
            default:
                return false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showDialog() {
        initDatePicker();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        dialog = new Dialog(HomeActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_add_pasien);
        dialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = height;
        dialog.getWindow().setAttributes(layoutParams);

        Button btnSimpan = dialog.findViewById(R.id.btn_simpan);
        btnTanggalLahir = dialog.findViewById(R.id.btn_tanggal_lahir);

        EditText etNamaLengkap, etNomorTelepon, etAlamat;
        etNamaLengkap = dialog.findViewById(R.id.et_nama_lengkap);
        etNomorTelepon = dialog.findViewById(R.id.et_nomor_telepon);
        etAlamat = dialog.findViewById(R.id.et_alamat);

        Spinner spinnerGender = dialog.findViewById(R.id.spinner_jenis_kelamin);
        String[] gender = {getString(R.string.gender_laki), getString(R.string.gender_perempuan)};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_text_style, gender);
        spinnerGender.setAdapter(arrayAdapter);

        btnTanggalLahir.setFocusable(true);
        btnTanggalLahir.setFocusableInTouchMode(true);
        btnTanggalLahir.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                datePickerDialog.show();
            }
        });

        btnTanggalLahir.setOnClickListener(v -> {
            datePickerDialog.show();
        });

        // BUTTON SIMPAN
        btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            nama = String.valueOf(etNamaLengkap.getText());
            kelamin = String.valueOf(spinnerGender.getSelectedItem());
            telepon = String.valueOf(etNomorTelepon.getText());
            alamat = String.valueOf(etAlamat.getText());
            tanggal_lahir = String.valueOf(btnTanggalLahir.getText());
            postData(nama, kelamin, telepon, alamat, tanggal_lahir);
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String tanggalLahir = dayOfMonth + "/" + month + "/" + year;
            btnTanggalLahir.setText(tanggalLahir);

            calculateAge(year, month, dayOfMonth);
        };
        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, dateSetListener, tahun, bulan, hari);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
    }

    private void postData(String nama, String kelamin, String telepon, String alamat, String tanggalLahir) {
        // creating a collection reference
        // for our Firebase Firetore database.
        CollectionReference dbPasien = db.collection("pasien");

        // adding our data to our courses object class.
        PasienModel pasienModel = new PasienModel(nama, kelamin, telepon, alamat, tanggalLahir);

        // POST TO "pasien" COLLECTION
        dbPasien.add(pasienModel).addOnSuccessListener(documentReference -> {
            Log.d("SUCCESS", "Data terkirim: " + nama + kelamin + telepon + alamat);
            Toast.makeText(HomeActivity.this, "Data terkirim.", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_home, ByNameFragment.class, null)
                    .commit();
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
            Toast.makeText(HomeActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
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