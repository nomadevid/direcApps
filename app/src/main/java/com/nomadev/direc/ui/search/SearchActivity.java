package com.nomadev.direc.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.databinding.ActivitySearchBinding;
import com.nomadev.direc.model.PasienModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchAdapter adapter;
    private FirebaseFirestore db;
    private SearchViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        showProgressBar(false);

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchPasien(binding.etSearch.getText().toString());
            }
            return false;
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                showProgressBar(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showProgressBar(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                showProgressBar(false);
                searchPasien(s.toString());
            }
        });

        getPasienData();
    }


    private void getPasienData() {
        CollectionReference dbPasien = db.collection("pasien");
        Query query = dbPasien.orderBy("nama", Query.Direction.ASCENDING);
        ArrayList<PasienModel> listPasien = new ArrayList<>();

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
                addPasienLocal(listPasien);
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
            }
        });
    }

    private void addPasienLocal(List<PasienModel> list) {
        viewModel.addPasien(list);
    }

    private void searchPasien(String query) {
        String searchQuery = "%" + query + "%";
        List<String> listId = new ArrayList<>();
        viewModel.searchPatient(searchQuery).observe(this, list -> {
            if (!list.isEmpty()) {
                listId.clear();
                for (int i = 0; i < list.size(); i++) {
                    listId.add(list.get(i).getId());
                }
                Log.d("SearchActivity", listId.toString());
                getSearchPasien(listId);
                showSearchInfo(false);
            } else {
                showSearchInfo(true);
                binding.rvSearch.setVisibility(View.GONE);
            }
        });
    }

    private void getSearchPasien(List<String> listId) {
        viewModel.getSearchPatients(listId).observe(this, list -> {
            if (list.size() > 0) {
                adapter = new SearchAdapter(list);
                showRecyclerView();
            }
        });
    }


    private void showRecyclerView() {
        binding.rvSearch.setVisibility(View.VISIBLE);
        binding.rvSearch.setHasFixedSize(true);
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearch.setAdapter(adapter);
    }

    private void showProgressBar(Boolean state) {
        if (state) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void showSearchInfo(Boolean state) {
        if (state) {
            binding.tvSearchInfo.setVisibility(View.VISIBLE);
        } else {
            binding.tvSearchInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}