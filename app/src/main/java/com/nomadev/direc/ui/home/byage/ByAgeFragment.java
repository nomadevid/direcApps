package com.nomadev.direc.ui.home.byage;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.databinding.FragmentByAgeBinding;
import com.nomadev.direc.model.PasienModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ByAgeFragment extends Fragment {

    private FragmentByAgeBinding binding;
    private FirebaseFirestore db;
    private ArrayList<PasienModel> listPasien;
    private ArrayList<PasienModel> listPasienSection;
    private ByAgeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentByAgeBinding.inflate(inflater, container, false);
        showProgressBar(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        listPasien = new ArrayList<>();
        listPasienSection = new ArrayList<>();
        adapter = new ByAgeAdapter(listPasienSection);

        binding.refreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            listPasien.clear();
            Log.d("listPasien: ", listPasien.toString());
            getPasienData();
            showRecyclerView();
        }, 2000));

        showProgressBar(true);
        getPasienData();
        showRecyclerView();
    }

    private void getPasienData() {
        CollectionReference dbPasien = db.collection("pasien");
        Query query = dbPasien.orderBy("nama", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            showProgressBar(false);
            Log.d("queryDocumentSnapshots", queryDocumentSnapshots.toString());
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    Log.d("SNAPSHOT", d.toString());
                    PasienModel pasienModel = d.toObject(PasienModel.class);
                    if (pasienModel != null) {
                        pasienModel.setId(d.getId());
                        pasienModel.isSection = false;

                        String tanggalLahir = pasienModel.getTanggalLahir();

                        // KONVERSI STRING KE DATE
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        try {
                            Date date = format.parse(tanggalLahir);

                            // HITUNG USIA
                            Calendar dob = Calendar.getInstance();
                            Calendar today = Calendar.getInstance();

                            if (date != null) {
                                dob.setTime(date);
                                int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                                    age--;
                                }

                                String ageString = String.valueOf(age);
                                Log.d("usia", ageString);
                                pasienModel.setTanggalLahir(ageString);
                            }
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                        listPasien.add(pasienModel);
                    }
                }
                adapter.notifyDataSetChanged();
                getHeaderList(listPasien);
                showInfo(false);
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
            } else {
                showInfo(true);
                Log.d("FEEDBACK", "Data Kosong.");
            }
            binding.refreshLayout.setRefreshing(false);
        }).addOnFailureListener(e -> {
            showProgressBar(true);
            Log.d("FEEDBACK", "Error: " + e.toString());
            binding.refreshLayout.setRefreshing(false);
        });
    }

    private void getHeaderList(ArrayList<PasienModel> usersList) {
        Collections.sort(usersList, new Comparator<PasienModel>() {
            @Override
            public int compare(PasienModel user1, PasienModel user2) {
                return String.valueOf(user1.getTanggalLahir()).compareTo(String.valueOf(user2.getTanggalLahir()));
            }
        });

        String lastHeader = "";
        int size = usersList.size();
        listPasienSection.clear();

        for (int i = 0; i < size; i++) {
            PasienModel user = usersList.get(i);
            Log.d("getHeader", user.getTanggalLahir());
            String header = String.valueOf(user.getTanggalLahir());

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                listPasienSection.add(new PasienModel("", "", "", "", header, true));
            }
            listPasienSection.add(user);
        }
    }

    private void showRecyclerView() {
        binding.rvByAge.setHasFixedSize(true);
        binding.rvByAge.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvByAge.setAdapter(adapter);
    }

    private void showProgressBar(Boolean state) {
        if (state) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void showInfo(Boolean state) {
        if (state) {
            binding.tvKeterangan.setVisibility(View.VISIBLE);
        } else {
            binding.tvKeterangan.setVisibility(View.GONE);
        }
    }


}