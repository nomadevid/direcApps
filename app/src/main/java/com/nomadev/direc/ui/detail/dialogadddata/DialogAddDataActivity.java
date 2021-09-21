package com.nomadev.direc.ui.detail.dialogadddata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddDataBinding;
import com.nomadev.direc.databinding.ActivityDialogAddPasienBinding;
import com.nomadev.direc.model.FotoModel;
import com.nomadev.direc.model.HasilPeriksaModel;
import com.nomadev.direc.model.HistoryModel;
import com.nomadev.direc.model.PasienModel;
import com.nomadev.direc.ui.detail.FotoAdapter;
import com.nomadev.direc.ui.home.byname.ByNameFragment;

import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DialogAddDataActivity extends DialogFragment {

    private static final int PICK_IMAGE = 1;
    private ActivityDialogAddDataBinding binding;
    private FirebaseFirestore db;
    private LayoutInflater inflaterGlobal;
    private LinearLayout photo;
    private String idHistory;

    private final String ID = "id";
    private final String NAMA = "nama";
    private final String TANGGAL_LAHIR = "tanggal_lahir";
    private String keluhan, hasil_periksa, terapi, id, nama, tanggalLahir;
    private Uri ImageUri;
    private ArrayList ImageList = new ArrayList();
    private ArrayList<FotoModel> fotoModelArrayList;
    private FotoAdapter fotoAdapter;
    private ArrayList urlStrings;
    private int upload_count = 0;
    private DialogAddDataListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogAddDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        photo = binding.svFoto;
        inflaterGlobal = inflater;

        db = FirebaseFirestore.getInstance();
        id = getArguments().getString(ID);
        nama = getArguments().getString(NAMA);
        tanggalLahir = getArguments().getString(TANGGAL_LAHIR);

        fotoModelArrayList = new ArrayList<>();
        fotoAdapter = new FotoAdapter(this.fotoModelArrayList);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_add_data);
        getDialog().show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
//        layoutParams.height = height;
        getDialog().getWindow().setAttributes(layoutParams);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvFoto.setLayoutManager(gridLayoutManager);
        binding.rvFoto.setAdapter(fotoAdapter);
        showProgressBar(false);

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
            postData(hasil_periksa, keluhan, terapi, id);
            if (ImageList.isEmpty()) getDialog().dismiss();
            else postImage();
        });

        binding.ibAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE);
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.RefreshLayout(true);
        showProgressBar(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {


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

                        ImageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                        ImageList.add(ImageUri);

                        FotoModel fotoModel = new FotoModel();
                        fotoModel.setFoto(ImageUri);
                        fotoModelArrayList.add(fotoModel);

                        currentImageSlect = currentImageSlect + 1;
                    }
                    fotoAdapter.notifyDataSetChanged();

//                    Toast.makeText(getActivity(), "You have selected " + ImageList.size() + " Images", Toast.LENGTH_SHORT).show();


                } else {
//                    Toast.makeText(getActivity(), "Please Select Multiple Images", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    private void postImage() {

        urlStrings = new ArrayList<>();
        Log.d("IMAGE", "1");
        ArrayList<FotoModel> fotoModelListUpdate = fotoAdapter.getFotoModelList();
//        progressDialog.show();
//        alert.setText("If Loading Takes to long press button again");
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child(idHistory);

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
    }

    private void storeLink(ArrayList<String> urlStrings) {

        for (int i = 0; i < urlStrings.size(); i++) {

            DocumentReference dbData = db.collection("pasien").document(id).collection("history").document(idHistory);

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
        getDialog().dismiss();

        ImageList.clear();
    }

    private void postData(String hasil_periksa, String keluhan, String terapi, String id) {
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
        //HasilPeriksaModel hasilPeriksaModel = new HasilPeriksaModel(hasil_periksa, keluhan, tanggal, terapi);

        Map map = new HashMap<>();
        map.put("id", id);
        map.put("hasil_periksa", hasil_periksa);
        map.put("keluhan", keluhan);
        map.put("terapi", terapi);
        map.put("tanggal", tanggal);
        map.put("timeStamp", timestamp);

        // POST TO "pasien" COLLECTION
        dbData.set(map).addOnSuccessListener(documentReference -> {
            Log.d("SUCCESS", "Data terkirim: " + hasil_periksa + keluhan + tanggal + terapi);
            //Toast.makeText(getActivity(), "Data terkirim.", Toast.LENGTH_SHORT).show();
            postHistoryData(nama, id, tanggal, tanggalLahir, timestamp);
        }).addOnFailureListener(e -> {
            Log.d("GAGAL", "Error: " + e.toString());
            //Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
//            getActivity();
        });
    }

    private void postHistoryData(String nama, String idPasien, String addDate, String tanggalLahir, Timestamp timestamp) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = tf.format(c);

        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbPasien = db.collection("history_pasien").document(addDate).collection(addDate).document(idHistory);

        // adding our data to our courses object class.
        HistoryModel historyModel = new HistoryModel(idPasien, nama, addDate, time, tanggalLahir);
        Map map = new HashMap<>();
        map.put("idHistory", idHistory);
        map.put("idPasien", idPasien);
        map.put("nama", nama);
        map.put("addDate", addDate);
        map.put("addTime", time);
        map.put("tanggalLahir", tanggalLahir);
        map.put("timeStamp", timestamp);

        // POST TO COLLECTION
        dbPasien.set(map).addOnSuccessListener(documentReference -> {
            Log.d("postHistoryData", "Data terkirim.");

        }).addOnFailureListener(e -> {
            Log.d("postHistoryData", "Error: " + e.toString());
        });
    }

    private void showProgressBar(Boolean state) {
        if (state) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.rlProgress.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#40000000")));
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.rlProgress.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DialogAddDataListener) context;
    }

    public interface DialogAddDataListener {
        void RefreshLayout(Boolean state);
    }
}