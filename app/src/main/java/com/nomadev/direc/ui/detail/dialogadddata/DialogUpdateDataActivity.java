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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddDataBinding;
import com.nomadev.direc.model.FotoModel;
import com.nomadev.direc.ui.detail.FotoAdapter;
import com.nomadev.direc.ui.detail.FotoStreamUpdateAdapater;
import com.nomadev.direc.ui.detail.HasilPeriksaAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogUpdateDataActivity extends DialogFragment {

    private ActivityDialogAddDataBinding binding;
    private FirebaseFirestore db;
    private String id_data, id_pasien, keluhan, hasil_periksa, terapi, tanggal;
    private final ArrayList<Uri> ImageList = new ArrayList<>();
    private ArrayList<FotoModel> fotoModelArrayList;
    private FotoAdapter fotoAdapter;
    private ArrayList<String> urlStrings;
    private FotoStreamUpdateAdapater fotoStreamUpdateAdapater;
    private DialogUpdateDataListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogAddDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            id_data = getArguments().getString(HasilPeriksaAdapter.ViewHolder.ID_DATA);
            id_pasien = getArguments().getString(HasilPeriksaAdapter.ViewHolder.ID_PASIEN);
        }

        fotoModelArrayList = new ArrayList<>();
        fotoAdapter = new FotoAdapter(this.fotoModelArrayList);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);

        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setContentView(R.layout.activity_dialog_update_data);
            getDialog().show();
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        getDialog().getWindow().setAttributes(layoutParams);

        binding.tvTitle.setText(getString(R.string.ubah_data));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvFoto.setLayoutManager(gridLayoutManager);
        binding.rvFoto.setAdapter(fotoAdapter);
        showProgressBar(false);
        getData();

        //Simpan
        binding.btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            keluhan = String.valueOf(binding.etKeluhan.getText());
            hasil_periksa = String.valueOf(binding.etHasilPeriksa.getText());
            terapi = String.valueOf(binding.etTerapi.getText());

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

            showProgressBar(true);
            binding.btnSimpan.setClickable(false);
            binding.btnSimpan.setEnabled(false);
            updateData(hasil_periksa, keluhan, terapi);
            updateHistoryAll(tanggal, hasil_periksa, keluhan, terapi);
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

    @SuppressLint("NotifyDataSetChanged")
    private void getData() {
        DocumentReference dbPasien = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        dbPasien.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                keluhan = documentSnapshot.getString("keluhan");
                hasil_periksa = documentSnapshot.getString("hasil_periksa");
                terapi = documentSnapshot.getString("terapi");
                tanggal = documentSnapshot.getString("tanggal");

                binding.etKeluhan.setText(keluhan);
                binding.etHasilPeriksa.setText(hasil_periksa);
                binding.etTerapi.setText(terapi);

                if (documentSnapshot.get("foto") != null) {
                    fotoStreamUpdateAdapater = new FotoStreamUpdateAdapater((ArrayList<String>) documentSnapshot.get("foto"), id_pasien, id_data);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
                    gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    binding.rvFotoStream.setLayoutManager(gridLayoutManager);
                    binding.rvFotoStream.setAdapter(fotoStreamUpdateAdapater);
                    fotoStreamUpdateAdapater.notifyDataSetChanged();
                }

                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show());

    }

    private void postImage() {

        urlStrings = new ArrayList<>();
        Log.d("IMAGE", "1");
        ArrayList<FotoModel> fotoModelListUpdate = fotoAdapter.getFotoModelList();
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child(id_data);

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

            DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

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

    private void updateData(String hasil_periksa, String keluhan, String terapi) {
        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        // adding our data to our courses object class.
//        HasilPeriksaModel updatedDataModel = new HasilPeriksaModel(hasil_periksa, keluhan, tanggal, terapi);

        // UPDATE TO "id" DOCUMENT
        dbData.update(
                "hasil_periksa", hasil_periksa,
                "keluhan", keluhan,
                "tanggal", tanggal,
                "terapi", terapi
        ).addOnSuccessListener(unused -> Log.d("SUCCESS", "Data terkirim: " + hasil_periksa + keluhan + tanggal + terapi))
                .addOnFailureListener(e -> Log.d("GAGAL", "Error: " + e.toString()));
    }

    private void updateHistoryAll(String tanggalLahir, String hasil_periksa, String keluhan, String terapi) {

        DocumentReference dbRef = db.collection("history_pasien_all").document(id_data);

        Map<String, Object> map = new HashMap<>();
        map.put("tanggalLahir", tanggalLahir);
        map.put("hasil_periksa", hasil_periksa);
        map.put("keluhan", keluhan);
        map.put("terapi", terapi);

        dbRef.update(map)
                .addOnSuccessListener(unused -> Log.d("postHistoryAll", "Data terkirim."))
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
        listener = (DialogUpdateDataListener) context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.RefreshLayout(true);
        showProgressBar(false);
    }

    public interface DialogUpdateDataListener {
        void RefreshLayout(Boolean state);
    }
}