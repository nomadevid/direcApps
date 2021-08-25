package com.nomadev.direc.ui.detail.dialogadddata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddDataBinding;
import com.nomadev.direc.databinding.ActivityDialogAddPasienBinding;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.home.byname.ByNameFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DialogAddDataActivity extends DialogFragment {

    private ActivityDialogAddDataBinding binding;
    private FirebaseFirestore db;

    private final String ID = "id";
    private String keluhan, hasil_periksa, terapi, id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogAddDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        id = getArguments().getString(ID);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_add_pasien);
        getDialog().show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = height;
        getDialog().getWindow().setAttributes(layoutParams);

        //Simpan
        binding.btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            keluhan = String.valueOf(binding.etKeluhan.getText());
            hasil_periksa = String.valueOf(binding.etHasilPeriksa.getText());
            terapi = String.valueOf(binding.etTerapi.getText());

            postData(hasil_periksa, keluhan, terapi, id);
        });

        return view;
    }

    private void postData(String hasil_periksa, String keluhan, String terapi, String id) {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String tanggal = df.format(c);
        // creating a collection reference
        // for our Firebase Firetore database.
        CollectionReference dbData = db.collection("pasien").document(id).collection("history");

        // adding our data to our courses object class.
        HasilPeriksaModel hasilPeriksaModel = new HasilPeriksaModel(hasil_periksa, keluhan, tanggal, terapi);

        // POST TO "pasien" COLLECTION
        dbData.add(hasilPeriksaModel).addOnSuccessListener(documentReference -> {
            Log.d("SUCCESS", "Data terkirim: " + hasil_periksa + keluhan + tanggal + terapi);
            Toast.makeText(getActivity(), "Data terkirim.", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_home, ByNameFragment.class, null)
                    .commit();
            getDialog().dismiss();
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });
    }
}