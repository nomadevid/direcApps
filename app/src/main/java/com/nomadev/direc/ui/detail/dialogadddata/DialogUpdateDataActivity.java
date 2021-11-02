package com.nomadev.direc.ui.detail.dialogadddata;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogAddDataBinding;
import com.nomadev.direc.function.MoneyTextWatcher;
import com.nomadev.direc.model.FotoModel;
import com.nomadev.direc.ui.detail.FotoAdapter;
import com.nomadev.direc.ui.detail.FotoStreamUpdateAdapater;
import com.nomadev.direc.ui.detail.HasilPeriksaAdapter;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogUpdateDataActivity extends DialogFragment {

    private ActivityDialogAddDataBinding binding;
    private FirebaseFirestore db;
    private String id_data, id_pasien, keluhan, hasil_periksa, terapi, tanggal, pemeriksa, tagihan, penyakit;
    private final ArrayList<Uri> ImageList = new ArrayList<>(), schemeList = new ArrayList<>();
    private final ArrayList<String> schemeUrlList = new ArrayList<>();
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
        schemeList.add(0, null);
        schemeList.add(1, null);

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

        binding.etTagihan.addTextChangedListener(new MoneyTextWatcher(binding.etTagihan));

        String[] penyakitList = {getString(R.string.penyakit_katarak), getString(R.string.penyakit_pterygium), getString(R.string.penyakit_hordeolum), getString(R.string.penyakit_Uveitis), getString(R.string.penyakit_poag), getString(R.string.penyakit_pacg), getString(R.string.penyakit_kelainan_refraksi), getString(R.string.penyakit_ulkus_kornea), getString(R.string.penyakit_dry_eye_disease), getString(R.string.penyakit_lainnya)};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_text_style, penyakitList);
        binding.spinnerPenyakit.setAdapter(arrayAdapter);

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
            penyakit = binding.spinnerPenyakit.getSelectedItem().toString();
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

            if (bill_int < 100000) {
                binding.etTagihan.setError("Minimaum Tagihan Rp 100.000");
                return;
            }

            showProgressBar(true);
            binding.btnSimpan.setClickable(false);
            binding.btnSimpan.setEnabled(false);

            updateHistoryAll(tanggal, hasil_periksa, keluhan, terapi);
            updateData(pemeriksa, penyakit, hasil_periksa, keluhan, terapi, bill_int);
            postScheme();
            if (ImageList.isEmpty()) getDialog().dismiss();
            else postImage();
        });

        binding.ibAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            activityResultLauncher.launch(intent);
        });

        binding.tbAddScheme1.setOnClickListener(v -> {
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.skema1)
                    + '/' + getResources().getResourceTypeName(R.drawable.skema1) + '/'
                    + getResources().getResourceEntryName(R.drawable.skema1));
            Intent intent = new Intent(getActivity(), DsPhotoEditorActivity.class);
            intent.setData(imageUri);
            //Set ToolBar
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
                    "Direc");
            intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR,
                    Color.parseColor("#FF12B69E"));
            intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR,
                    Color.parseColor("#DDDDDD"));
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
                    new int[]{0, 1, 2, 3, 4, 5, 8, 9, 10, 11});
            //Start Activity
            startActivityForResult(intent, 101);
            Log.d("TAG", "onCreateView: clicked");
        });

        binding.tbAddScheme2.setOnClickListener(v -> {
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.skema2)
                    + '/' + getResources().getResourceTypeName(R.drawable.skema2) + '/'
                    + getResources().getResourceEntryName(R.drawable.skema2));
            Intent intent = new Intent(getActivity(), DsPhotoEditorActivity.class);
            intent.setData(imageUri);
            //Set ToolBar
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
                    "Direc");
            intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR,
                    Color.parseColor("#FF12B69E"));
            intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR,
                    Color.parseColor("#DDDDDD"));
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
                    new int[]{0, 1, 2, 3, 4, 5, 8, 9, 10, 11});
            //Start Activity
            startActivityForResult(intent, 102);
            Log.d("TAG", "onCreateView: clicked");
        });

        binding.ivScheme1.setOnLongClickListener(v -> {
            deleteScheme(0);
            return true;
        });

        binding.ivScheme2.setOnLongClickListener(v -> {
            deleteScheme(1);
            return true;
        });

        return view;
    }

    private void deleteScheme(int i) {
        StorageReference deleteImage;
        if (i == 0) {
            deleteImage = FirebaseStorage.getInstance().getReferenceFromUrl(schemeUrlList.get(0));
            deleteImage.delete().addOnSuccessListener(unused -> {
                Toast.makeText(getActivity(), "Foto Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                Log.d("SUCCESS", "onSuccess: " + schemeUrlList.get(0));
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Foto Gagal Dihapus", Toast.LENGTH_SHORT).show();
                Log.d("FAIL", "onFailur: " + e.toString());
            });
            schemeUrlList.set(0, null);
            schemeList.add(0, null);
            binding.ivScheme1.setVisibility(View.GONE);
            binding.tbAddScheme1.setText(R.string.tambah_skema_1);
        } else {
            deleteImage = FirebaseStorage.getInstance().getReferenceFromUrl(schemeUrlList.get(1));
            deleteImage.delete().addOnSuccessListener(unused -> {
                Toast.makeText(getActivity(), "Foto Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                Log.d("SUCCESS", "onSuccess: " + schemeUrlList.get(1));
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Foto Gagal Dihapus", Toast.LENGTH_SHORT).show();
                Log.d("FAIL", "onFailur: " + e.toString());
            });
            schemeUrlList.set(1, null);
            schemeList.add(1, null);
            binding.ivScheme2.setVisibility(View.GONE);
            binding.tbAddScheme1.setText(R.string.tambah_skema_2);
        }
        Toast.makeText(getActivity(), "Skema Dihapus", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            if (requestCode == 101) {
                schemeList.add(0, uri);
                binding.ivScheme1.setImageURI(uri);
                binding.ivScheme1.setVisibility(View.VISIBLE);
                binding.tbAddScheme1.setText(R.string.ganti_skema_1);
            }
            if (requestCode == 102) {
                schemeList.add(1, uri);
                binding.ivScheme2.setImageURI(uri);
                binding.ivScheme2.setVisibility(View.VISIBLE);
                binding.tbAddScheme1.setText(R.string.ganti_skema_2);
            }
        }
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
                tagihan = String.valueOf(documentSnapshot.getLong("tagihan"));
                pemeriksa = documentSnapshot.getString("pemeriksa");
                schemeUrlList.add(0, documentSnapshot.getString("skema1"));
                schemeUrlList.add(1, documentSnapshot.getString("skema2"));
                penyakit = documentSnapshot.getString("penyakit");
                getPenyakit();

                if (schemeUrlList.get(0) != null) {
                    Picasso.get().load(schemeUrlList.get(0)).fit().into(binding.ivScheme1);
                    binding.tbAddScheme1.setText(R.string.ganti_skema_1);
                } else binding.ivScheme1.setVisibility(View.GONE);

                if (schemeUrlList.get(1) != null) {
                    Picasso.get().load(schemeUrlList.get(1)).fit().into(binding.ivScheme2);
                    binding.tbAddScheme2.setText(R.string.ganti_skema_2);
                } else binding.ivScheme2.setVisibility(View.GONE);


                binding.etKeluhan.setText(keluhan);
                binding.etHasilPeriksa.setText(hasil_periksa);
                binding.etTerapi.setText(terapi);
                binding.tvPemeriksa.setText(pemeriksa);
                binding.etTagihan.setText(tagihan);

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

    private void getPenyakit() {
        if (penyakit.equals(getString(R.string.penyakit_katarak)))
            binding.spinnerPenyakit.setSelection(0);
        else if (penyakit.equals(getString(R.string.penyakit_pterygium)))
            binding.spinnerPenyakit.setSelection(1);
        else if (penyakit.equals(getString(R.string.penyakit_hordeolum)))
            binding.spinnerPenyakit.setSelection(2);
        else if (penyakit.equals(getString(R.string.penyakit_Uveitis)))
            binding.spinnerPenyakit.setSelection(3);
        else if (penyakit.equals(getString(R.string.penyakit_poag)))
            binding.spinnerPenyakit.setSelection(4);
        else if (penyakit.equals(getString(R.string.penyakit_pacg)))
            binding.spinnerPenyakit.setSelection(5);
        else if (penyakit.equals(getString(R.string.penyakit_kelainan_refraksi)))
            binding.spinnerPenyakit.setSelection(6);
        else
            binding.spinnerPenyakit.setSelection(7);
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

    private void postScheme() {
        Uri IndividuScheme;
        DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);
        StorageReference SchemeFolder = FirebaseStorage.getInstance().getReference().child(id_data).child("scheme");

        dbData.update(
                "skema1", schemeUrlList.get(0),
                "skema2", schemeUrlList.get(1)
        ).addOnSuccessListener(unused -> Log.d("SUCCESS", "Foto terkirim: " + schemeList.get(0)))
                .addOnFailureListener(e -> Log.e("FAILURE", "ERROR : " + e.toString()));

        if (schemeList.get(0) != null) {
            IndividuScheme = schemeList.get(0);

            StorageReference SchemeName = SchemeFolder.child("Scheme" + IndividuScheme.getLastPathSegment());

            SchemeName.putFile(IndividuScheme).addOnSuccessListener(
                    taskSnapshot -> SchemeName.getDownloadUrl().addOnSuccessListener(uri ->
                            dbData.update(
                                    "skema1", String.valueOf(uri)
                            ).addOnSuccessListener(unused -> Log.d("SUCCESS", "Foto terkirim: " + uri))
                                    .addOnFailureListener(e -> Log.e("FAILURE", "ERROR : " + e.toString()))));
        }

        if (schemeList.get(1) != null) {
            IndividuScheme = schemeList.get(1);

            StorageReference SchemeName = SchemeFolder.child("Scheme" + IndividuScheme.getLastPathSegment());

            SchemeName.putFile(IndividuScheme).addOnSuccessListener(
                    taskSnapshot -> SchemeName.getDownloadUrl().addOnSuccessListener(uri ->
                            dbData.update(
                                    "skema2", String.valueOf(uri)
                            ).addOnSuccessListener(unused -> Log.d("SUCCESS", "Foto terkirim: " + uri))
                                    .addOnFailureListener(e -> Log.e("FAILURE", "ERROR : " + e.toString()))));
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

    private void updateData(String pemeriksa, String penyakit, String hasil_periksa, String keluhan, String terapi, int tagihan) {
        // creating a collection reference
        // for our Firebase Firetore database.
        DocumentReference dbData = db.collection("pasien").document(id_pasien).collection("history").document(id_data);

        // adding our data to our courses object class.
//        HasilPeriksaModel updatedDataModel = new HasilPeriksaModel(hasil_periksa, keluhan, tanggal, terapi);

        // UPDATE TO "id" DOCUMENT
        dbData.update(
                "pemeriksa", pemeriksa,
                "penyakit", penyakit,
                "hasil_periksa", hasil_periksa,
                "keluhan", keluhan,
                "tanggal", tanggal,
                "terapi", terapi,
                "tagihan", tagihan
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
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            listener.RefreshLayout(true);
            showProgressBar(false);
        }, 2000);
    }

    public interface DialogUpdateDataListener {
        void RefreshLayout(Boolean state);
    }
}