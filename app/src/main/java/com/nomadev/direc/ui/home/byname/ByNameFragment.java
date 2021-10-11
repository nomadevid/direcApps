package com.nomadev.direc.ui.home.byname;

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
import com.nomadev.direc.databinding.FragmentByNameBinding;
import com.nomadev.direc.model.PasienModel;
import com.viethoa.models.AlphabetItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ByNameFragment extends Fragment {
    private FragmentByNameBinding binding;
    private FirebaseFirestore db;
    private ArrayList<PasienModel> listPasien;
    private ArrayList<PasienModel> listPasienSection;
    private ByNameAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentByNameBinding.inflate(inflater, container, false);
        showProgressBar(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        listPasien = new ArrayList<>();
        listPasienSection = new ArrayList<>();
        adapter = new ByNameAdapter(listPasienSection);

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
                        listPasien.add(pasienModel);
                    }
                }
                adapter.notifyDataSetChanged();
                getHeaderListLatter(listPasien);
                showInfo(false);
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
            } else {
                showInfo(true);
                Log.d("FEEDBACK", "Data Kosong.");
            }
            binding.refreshLayout.setRefreshing(false);
        }).addOnFailureListener(e -> {
            showProgressBar(false);
            Log.d("FEEDBACK", "Error: " + e.toString());
            binding.refreshLayout.setRefreshing(false);
        });
    }

    private void getHeaderListLatter(ArrayList<PasienModel> usersList) {
        Collections.sort(usersList, new Comparator<PasienModel>() {
            @Override
            public int compare(PasienModel user1, PasienModel user2) {
                return String.valueOf(user1.getNama().charAt(0)).toUpperCase().compareTo(String.valueOf(user2.getNama().charAt(0)).toUpperCase());
            }
        });

        String lastHeader = "";
        int size = usersList.size();
        listPasienSection.clear();

        for (int i = 0; i < size; i++) {
            PasienModel user = usersList.get(i);
            Log.d("getHeader", user.getNama());
            String header = String.valueOf(user.getNama().charAt(0)).toUpperCase();

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                listPasienSection.add(new PasienModel(header, 0, "", "", "", true));
            }
            generateAlphabetItem(listPasienSection);
            listPasienSection.add(user);
        }
    }

    private void generateAlphabetItem(ArrayList<PasienModel> usersList) {
        List<AlphabetItem> mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < usersList.size(); i++) {
            String name = usersList.get(i).getNama().toUpperCase();
            Log.d("generateAlphabet", name);
            if (name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }

        binding.fastScroller.setRecyclerView(binding.rvByName);
        binding.fastScroller.setUpAlphabet(mAlphabetItems);
    }

    private void showRecyclerView() {
        binding.rvByName.setHasFixedSize(true);
        binding.rvByName.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvByName.setAdapter(adapter);
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