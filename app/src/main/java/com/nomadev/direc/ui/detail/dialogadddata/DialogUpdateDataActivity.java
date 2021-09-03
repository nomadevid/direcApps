package com.nomadev.direc.ui.detail.dialogadddata;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogUpdateDataBinding;
import com.nomadev.direc.databinding.ActivityDialogUpdatePasienBinding;
import com.nomadev.direc.model.FotoModel;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.FotoAdapter;
import com.nomadev.direc.ui.detail.FotoStreamAdapter;
import com.nomadev.direc.ui.detail.FotoStreamUpdateAdapater;

import java.util.ArrayList;

public class DialogUpdateDataActivity extends DialogFragment {

    private static final int PICK_IMAGE = 1;
    private ActivityDialogUpdateDataBinding binding;
    private FirebaseFirestore db;
    private final String ID_PASIEN = "id_pasien";
    private final String ID_DATA = "id_data";
    private String id_data, id_pasien, keluhan, hasil_periksa, terapi, tanggal;
    private Uri ImageUri;
    private ArrayList ImageList = new ArrayList();
    private ArrayList<FotoModel> fotoModelArrayList;
    private FotoAdapter fotoAdapter;
    private ArrayList urlStrings;
    private int upload_count = 0;
    private FotoStreamUpdateAdapater fotoStreamUpdateAdapater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogUpdateDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        id_data = getArguments().getString(ID_DATA);
        id_pasien = getArguments().getString(ID_PASIEN);

        fotoModelArrayList = new ArrayList<>();
        fotoAdapter = new FotoAdapter(this.fotoModelArrayList);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_update_data);
        getDialog().show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = height;
        getDialog().getWindow().setAttributes(layoutParams);

        binding.rvFoto.setAdapter(fotoAdapter);
        getData();

        //Simpan
        binding.btnSimpan.setOnClickListener(v -> {
            Log.d("BUTTON", "Button Pressed.");
            keluhan = String.valueOf(binding.etKeluhan.getText());
            hasil_periksa = String.valueOf(binding.etHasilPeriksa.getText());
            terapi = String.valueOf(binding.etTerapi.getText());

            updateData(hasil_periksa, keluhan, terapi);
            postImage();
        });

        binding.ibAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE);
        });

        return view;
    }

    private void getData() {
        DocumentReference dbPasien = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        dbPasien.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    keluhan = documentSnapshot.getString("keluhan");
                    hasil_periksa = documentSnapshot.getString("hasil_periksa");
                    terapi = documentSnapshot.getString("terapi");
                    tanggal = documentSnapshot.getString("tanggal");

                    binding.etKeluhan.setText(keluhan);
                    binding.etHasilPeriksa.setText(hasil_periksa);
                    binding.etTerapi.setText(terapi);

                    if (documentSnapshot.get("foto") != null) {
                        fotoStreamUpdateAdapater = new FotoStreamUpdateAdapater((ArrayList<String>) documentSnapshot.get("foto"));
                        binding.rvFotoStream.setAdapter(fotoStreamUpdateAdapater);
                        fotoStreamUpdateAdapater.notifyDataSetChanged();
                    }

                    Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                } else {
                    Log.d("FEEDBACK", "Data Kosong.");
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {


                if (data.getClipData() != null) {

                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSlect = 0;

                    while (currentImageSlect < countClipData) {

                        ImageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                        ImageList.add(ImageUri);

                        FotoModel fotoModel = new FotoModel();
                        fotoModel.setFoto(ImageUri);
                        fotoModelArrayList.add(fotoModel);

//                        View photoView = inflaterGlobal.inflate(R.layout.item_photo, photo, false);
//
//                        ImageView imageView = photoView.findViewById(R.id.iv_photo);
//                        imageView.setImageURI(ImageUri);
//
//                        photo.addView(photoView);

                        currentImageSlect = currentImageSlect + 1;
                    }
                    fotoAdapter.notifyDataSetChanged();

                    Toast.makeText(getActivity(), "You have selected " + ImageList.size() + " Images", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(getActivity(), "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    private void postImage() {

        DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);
        dbData.update(
                "foto", FieldValue.arrayRemove()
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("SUCCESS", "Field Value Reset: ");
            }
        }).addOnFailureListener(e -> Log.d("FAILURE", "ERROR : " + e.toString()));

        urlStrings = new ArrayList<>();
        Log.d("IMAGE", "1");
        ArrayList<FotoModel> fotoModelListUpdate = fotoAdapter.getFotoModelList();
//        progressDialog.show();
//        alert.setText("If Loading Takes to long press button again");
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child(id_data);

        Log.d("IMAGE", "2");
        for (upload_count = 0; upload_count < fotoModelListUpdate.size(); upload_count++) {

            FotoModel foto = fotoModelListUpdate.get(upload_count);

            Uri IndividualImage = foto.getFoto();
            StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());

            Log.d("IMAGE", "4");
            ImageName.putFile(IndividualImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageName.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            urlStrings.add(String.valueOf(uri));
                                            Log.d("URL", "onSuccess: " + urlStrings);

                                            // Buat Upload ke Firestore nanti
                                            if (urlStrings.size() == fotoModelListUpdate.size()) {
                                                storeLink(urlStrings);
                                            }

                                        }
                                    }
                            ).addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Gagal Mengirim Data", Toast.LENGTH_SHORT).show();
                                Log.d("FAIL", "onFailur: " + e.toString());
                            });
                        }
                    }
            );

            Log.d("IMAGE", "3");

        }
        storeLink(fotoStreamUpdateAdapater.getListImageUrl());
    }

    private void storeLink(ArrayList<String> urlStrings) {

        for (int i = 0; i < urlStrings.size(); i++) {

            DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

            int finalI = i;
            dbData.update(
                    "foto", FieldValue.arrayUnion(urlStrings.get(i))
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("SUCCESS", "Foto terkirim: " + urlStrings.get(finalI));

                }
            }).addOnFailureListener(e -> {
                Log.d("FAILURE", "ERROR : " + e.toString());

            });

        }
//        progressDialog.dismiss();
//        alert.setText("Uploaded Successfully");
//        uploaderBtn.setVisibility(View.GONE);

        ImageList.clear();
    }

    private void updateData(String hasil_periksa, String keluhan, String terapi) {
        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        // adding our data to our courses object class.
        HasilPeriksaModel updatedDataModel = new HasilPeriksaModel(hasil_periksa, keluhan, tanggal, terapi);

        // UPDATE TO "id" DOCUMENT
        dbData.update(
                "hasil_periksa", updatedDataModel.getHasil_periksa(),
                "keluhan", updatedDataModel.getKeluhan(),
                "tanggal", updatedDataModel.getTanggal(),
                "terapi", updatedDataModel.getTerapi()
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("SUCCESS", "Data terkirim: " + hasil_periksa + keluhan + tanggal + terapi);
                Toast.makeText(getActivity(), "Data terkirim.", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
                getActivity().recreate();
            }
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        });
    }
}