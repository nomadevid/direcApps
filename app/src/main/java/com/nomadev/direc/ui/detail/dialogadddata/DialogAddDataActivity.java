package com.nomadev.direc.ui.detail.dialogadddata;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
    private String penyakit, keluhan, hasil_periksa, terapi, id, nama, tanggalLahir, pemeriksa;
    private ArrayList<Uri> ImageList, schemeList;
    private ArrayList<FotoModel> fotoModelArrayList;
    private FotoAdapter fotoAdapter;
    private ArrayList<String> urlStrings;
    private DialogAddDataListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogAddDataBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            id = getArguments().getString(DetailActivity.ID);
            nama = getArguments().getString(DetailActivity.NAMA);
            tanggalLahir = getArguments().getString(DetailActivity.TANGGAL_LAHIR);
        }

        ImageList = new ArrayList<>();
        fotoModelArrayList = new ArrayList<>();
        fotoAdapter = new FotoAdapter(this.fotoModelArrayList);
        schemeList = new ArrayList<>();
        schemeList.add(0, null);
        schemeList.add(1, null);

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

        String[] penyakitList = {getString(R.string.penyakit_katarak), getString(R.string.penyakit_pterygium), getString(R.string.penyakit_hordeolum), getString(R.string.penyakit_Uveitis), getString(R.string.penyakit_poag), getString(R.string.penyakit_pacg), getString(R.string.penyakit_kelainan_refraksi), getString(R.string.penyakit_ulkus_kornea), getString(R.string.penyakit_dry_eye_disease), getString(R.string.penyakit_lainnya)};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_text_style, penyakitList);
        binding.spinnerPenyakit.setAdapter(arrayAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvFoto.setLayoutManager(gridLayoutManager);
        binding.rvFoto.setAdapter(fotoAdapter);
        showProgressBar(false);

        binding.etTagihan.addTextChangedListener(new MoneyTextWatcher(binding.etTagihan));
        binding.etTagihan.setText(R.string.minimum_tagihan);

        binding.ivScheme1.setVisibility(View.GONE);
        binding.ivScheme2.setVisibility(View.GONE);

        binding.tvPemeriksa.setText(pemeriksa);
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
            postData(pemeriksa, penyakit, hasil_periksa, keluhan, terapi, id, bill_int);
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
            schemeList.set(0, null);
            binding.ivScheme1.setVisibility(View.GONE);
            binding.tbAddScheme1.setText(R.string.tambah_skema_1);
            Toast.makeText(getActivity(), "Skema Dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });

        binding.ivScheme2.setOnLongClickListener(v -> {
            schemeList.set(1, null);
            binding.ivScheme2.setVisibility(View.GONE);
            binding.tbAddScheme1.setText(R.string.tambah_skema_2);
            Toast.makeText(getActivity(), "Skema Dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });

        getUser();

        return view;
    }

    private void getUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        String idUser;

        if (firebaseUser != null) {
            idUser = firebaseUser.getUid();

            CollectionReference collection = db.collection("users");
            DocumentReference dbRef = collection.document(idUser);

            dbRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        pemeriksa = task.getResult().getString("nama");
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            Uri uri = data.getData();
            if (requestCode == 101) {
                schemeList.set(0, uri);
                binding.ivScheme1.setImageURI(uri);
                binding.ivScheme1.setVisibility(View.VISIBLE);
                binding.tbAddScheme1.setText(R.string.ganti_skema_1);
            }
            if (requestCode == 102) {
                schemeList.set(1, uri);
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
                    if (resultCode == RESULT_OK) {
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

    private void postScheme() {
        Uri IndividuScheme;
        Log.d("SCHEME", "1");
        DocumentReference dbData = db.collection("pasien").document(id).collection("history").document(idHistory);
        StorageReference SchemeFolder = FirebaseStorage.getInstance().getReference().child(idHistory).child("scheme");

        Log.d("SCHEME", "2");

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

        SimpleDateFormat dfId = new SimpleDateFormat("ddMMyyhhmmssSS", Locale.getDefault());
        String idData = dfId.format(c);

        DocumentReference dbData = db.collection("pasien").document(id).collection("history").document();
        idHistory = dbData.getId();

        Map<Object, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("idData", idData);
        map.put("pemeriksa", pemeriksa);
        map.put("penyakit", penyakit);
        map.put("hasil_periksa", hasil_periksa);
        map.put("keluhan", keluhan);
        map.put("terapi", terapi);
        map.put("tanggal", tanggal);
        map.put("timeStamp", timestamp);
        map.put("tagihan", tagihan);
        map.put("skema1", null);
        map.put("skema2", null);

        dbData.set(map).addOnSuccessListener(documentReference -> {
            Log.d("SUCCESS", "Data terkirim: " + hasil_periksa + keluhan + tanggal + terapi);
            postHistoryAll(nama, penyakit, id, tanggal, tanggalLahir, hasil_periksa, keluhan, terapi, tagihan, timestamp);
        }).addOnFailureListener(e -> Log.d("GAGAL", "Error: " + e.toString()));
    }

    private void postHistoryAll(String nama, String penyakit, String idPasien, String addDate, String tanggalLahir, String hasil_periksa, String keluhan, String terapi, int tagihan, Timestamp timestamp) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = tf.format(c);

        DocumentReference dbRef = db.collection("history_pasien_all").document(idHistory);

        Map<Object, Object> map = new HashMap<>();
        map.put("idHistory", idHistory);
        map.put("idPasien", idPasien);
        map.put("nama", nama);
        map.put("penyakit", penyakit);
        map.put("addDate", addDate);
        map.put("addTime", time);
        map.put("tanggalLahir", tanggalLahir);
        map.put("hasil_periksa", hasil_periksa);
        map.put("keluhan", keluhan);
        map.put("terapi", terapi);
        map.put("tagihan", tagihan);
        map.put("timeStamp", timestamp);

        dbRef.set(map)
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
        listener = (DialogAddDataListener) context;
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

    public interface DialogAddDataListener {
        void RefreshLayout(Boolean state);
    }
}