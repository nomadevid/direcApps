package com.nomadev.direc.ui.detail.dialogadddata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddDataBinding;
import com.nomadev.direc.function.MoneyTextWatcher;
import com.nomadev.direc.model.FotoModel;
import com.nomadev.direc.ui.detail.DetailActivity;
import com.nomadev.direc.ui.detail.FotoAdapter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DialogAddDataActivity extends DialogFragment {

    private ActivityDialogAddDataBinding binding;
    private FirebaseFirestore db;
    private String idHistory;
    private int penyakitInteger;
    private String penyakit, keluhan, hasil_periksa, terapi, id, nama, tanggalLahir, pemeriksa;
    private ArrayList<Uri> ImageList;
    private ArrayList<FotoModel> fotoModelArrayList;
    private FotoAdapter fotoAdapter;
    private ArrayList<String> urlStrings;
    private DialogAddDataListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogAddDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        pemeriksa = "dr. Dyah Purwita Trianggadewi, M.ked.Klin, Sp.M";

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            id = getArguments().getString(DetailActivity.ID);
            nama = getArguments().getString(DetailActivity.NAMA);
            tanggalLahir = getArguments().getString(DetailActivity.TANGGAL_LAHIR);
        }

        ImageList = new ArrayList<>();
        fotoModelArrayList = new ArrayList<>();
        fotoAdapter = new FotoAdapter(this.fotoModelArrayList);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);

        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setContentView(R.layout.activity_dialog_add_data);
            getDialog().show();
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        getDialog().getWindow().setAttributes(layoutParams);

        String[] penyakitList = {getString(R.string.penyakit_katarak), getString(R.string.penyakit_pterygium), getString(R.string.penyakit_hordeolum), getString(R.string.penyakit_Uveitis), getString(R.string.penyakit_poag), getString(R.string.penyakit_pacg), getString(R.string.penyakit_kelainan_refraksi), getString(R.string.penyakit_lainnya)};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_text_style, penyakitList);
        binding.spinnerPenyakit.setAdapter(arrayAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvFoto.setLayoutManager(gridLayoutManager);
        binding.rvFoto.setAdapter(fotoAdapter);
        showProgressBar(false);

        binding.etTagihan.addTextChangedListener(new MoneyTextWatcher(binding.etTagihan));
        binding.etTagihan.setText(R.string.minimum_tagihan);

        binding.tvPemeriksa.setText(pemeriksa);
        //Simpan
        binding.btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            penyakitInteger = binding.spinnerPenyakit.getSelectedItemPosition();
            penyakit = String.valueOf(penyakitInteger);
            keluhan = String.valueOf(binding.etKeluhan.getText());
            hasil_periksa = String.valueOf(binding.etHasilPeriksa.getText());
            terapi = String.valueOf(binding.etTerapi.getText());
            //parse jumalh tagihan
            BigDecimal bill = MoneyTextWatcher.parseCurrencyValue(binding.etTagihan.getText().toString());
            int bill_int = bill.intValue();

            if (TextUtils.isEmpty(keluhan)) {
                binding.etKeluhan.setError("Masukkan Keluhan Pasien");
                return;
            }
            if (TextUtils.isEmpty(hasil_periksa)) {
                binding.etHasilPeriksa.setError("Masukkan Hasil Periksa Pasien");
                return;
            }
            if (TextUtils.isEmpty(terapi)) {
                binding.etTerapi.setError("Masukkan Terapi yang dilakukan Pasien");
                return;
            }

            if (bill_int < 100000){
                binding.etTagihan.setError("Minimaum Tagihan Rp 100.000");
                return;
            }

            showProgressBar(true);
            binding.btnSimpan.setClickable(false);
            binding.btnSimpan.setEnabled(false);
            postData(pemeriksa, penyakit, hasil_periksa, keluhan, terapi, id, bill_int);
            if (ImageList.isEmpty()) getDialog().dismiss();
            else postImage();
        });

        binding.ibAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            activityResultLauncher.launch(intent);
        });

        return view;
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    getActivity();
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            if (data.getData() != null) {
                                ImageList.add(data.getData());

                                FotoModel fotoModel = new FotoModel();
                                fotoModel.setFoto(data.getData());
                                fotoModelArrayList.add(fotoModel);

                                fotoAdapter.notifyDataSetChanged();

                                data.setData(null);
                            }

                            if (data.getClipData() != null) {

                                int countClipData = data.getClipData().getItemCount();
                                int currentImageSlect = 0;

                                while (currentImageSlect < countClipData) {

                                    Uri imageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                                    ImageList.add(imageUri);

                                    FotoModel fotoModel = new FotoModel();
                                    fotoModel.setFoto(imageUri);
                                    fotoModelArrayList.add(fotoModel);

                                    currentImageSlect = currentImageSlect + 1;
                                }
                                fotoAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                }
            }
    );

    private void postImage() {
        urlStrings = new ArrayList<>();
        Log.d("IMAGE", "1");
        ArrayList<FotoModel> fotoModelListUpdate = fotoAdapter.getFotoModelList();
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child(idHistory);

        Log.d("IMAGE", "2");
        for (int upload_count = 0; upload_count < fotoModelListUpdate.size(); upload_count++) {

            FotoModel foto = fotoModelListUpdate.get(upload_count);

            Uri IndividualImage = foto.getFoto();
            StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());

            Log.d("IMAGE", "4");
            ImageName.putFile(IndividualImage).addOnSuccessListener(
                    taskSnapshot -> ImageName.getDownloadUrl().addOnSuccessListener(
                            uri -> {
                                urlStrings.add(String.valueOf(uri));
                                Log.d("URL", "onSuccess: " + urlStrings);

                                // Buat Upload ke Firestore nanti
                                if (urlStrings.size() == fotoModelListUpdate.size()) {
                                    storeLink(urlStrings);
                                }

                            }
                    ).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Gagal Mengirim Data", Toast.LENGTH_SHORT).show();
                        Log.d("FAIL", "onFailur: " + e.toString());
                    })
            );
            Log.d("IMAGE", "3");
        }
    }

    private void storeLink(ArrayList<String> urlStrings) {

        for (int i = 0; i < urlStrings.size(); i++) {

            DocumentReference dbData = db.collection("pasien").document(id).collection("history").document(idHistory);

            int finalI = i;
            dbData.update(
                    "foto", FieldValue.arrayUnion(urlStrings.get(i))
            ).addOnSuccessListener(unused -> Log.d("SUCCESS", "Foto terkirim: " + urlStrings.get(finalI)))
                    .addOnFailureListener(e -> Log.d("FAILURE", "ERROR : " + e.toString()));
        }
        if (getDialog() != null) {
            getDialog().dismiss();
        }
        ImageList.clear();
    }

    private void postData(String pemeriksa, String penyakit, String hasil_periksa, String keluhan, String terapi, String id, int tagihan) {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String tanggal = df.format(c);
        Timestamp timestamp = new Timestamp(c);

        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbData = db.collection("pasien").document(id).collection("history").document();
        idHistory = dbData.getId();

        // adding our data to our courses object class.
        Map<Object, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("pemeriksa", pemeriksa);
        map.put("penyakit", penyakit);
        map.put("hasil_periksa", hasil_periksa);
        map.put("keluhan", keluhan);
        map.put("terapi", terapi);
        map.put("tanggal", tanggal);
        map.put("timeStamp", timestamp);
        map.put("tagihan", tagihan);

        // POST TO "pasien" COLLECTION
        dbData.set(map).addOnSuccessListener(documentReference -> {
            Log.d("SUCCESS", "Data terkirim: " + hasil_periksa + keluhan + tanggal + terapi);
            postHistoryData(nama, id, tanggal, tanggalLahir, timestamp);
        }).addOnFailureListener(e -> Log.d("GAGAL", "Error: " + e.toString()));
    }

    private void postHistoryData(String nama, String idPasien, String addDate, String tanggalLahir, Timestamp timestamp) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = tf.format(c);

        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbPasien = db.collection("history_pasien").document(addDate).collection(addDate).document(idHistory);

        // adding our data to our courses object class.
        Map<Object, Object> map = new HashMap<>();
        map.put("idHistory", idHistory);
        map.put("idPasien", idPasien);
        map.put("nama", nama);
        map.put("addDate", addDate);
        map.put("addTime", time);
        map.put("tanggalLahir", tanggalLahir);
        map.put("timeStamp", timestamp);

        // POST TO COLLECTION
        dbPasien.set(map).addOnSuccessListener(documentReference ->
                Log.d("postHistoryData", "Data terkirim."))
                .addOnFailureListener(e -> Log.d("postHistoryData", "Error: " + e.toString()));
    }

    private void showProgressBar(Boolean state) {
        if (state) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.rlProgress.setBackground(new ColorDrawable(Color.parseColor("#40000000")));
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.rlProgress.setBackground(new ColorDrawable(Color.parseColor("#00000000")));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DialogAddDataListener) context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.RefreshLayout(true);
        showProgressBar(false);
    }

    public interface DialogAddDataListener {
        void RefreshLayout(Boolean state);
    }
}