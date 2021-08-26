package com.nomadev.direc.ui.search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.databinding.ActivitySearchBinding;
import com.nomadev.direc.model.PasienModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ArrayList<PasienModel> searchList;
    private ArrayList<String> idList;
    private SearchAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        searchList = new ArrayList<>();
        idList = new ArrayList<>();
        adapter = new SearchAdapter(searchList, idList);

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //searchData(binding.etSearch.getText().toString());
                getSearchData(binding.etSearch.getText().toString());
            }
            return false;
        });
    }

    private void getSearchData(String input) {
        showProgressBar(true);
        Log.d("INPUT", input);
        idList.clear();
        searchList.clear();
        CollectionReference dbPasien = db.collection("pasien");
        Query query = dbPasien.orderBy("nama", Query.Direction.ASCENDING).startAt(input).endAt(input + "\uf8ff");

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("queryDocumentSnapshots", queryDocumentSnapshots.toString());
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    Log.d("SNAPSHOT", d.toString());
                    PasienModel pasienModel = d.toObject(PasienModel.class);
                    String id = pasienModel.setId(d.getId());
                    searchList.add(pasienModel);
                    idList.add(id);
                }
                adapter.notifyDataSetChanged();
                showRecyclerView();
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                Toast.makeText(this, "Berhasil Mengambil Data.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
                Toast.makeText(this, "Data Kosong.", Toast.LENGTH_SHORT).show();
                showRecyclerView();
            }
        }).addOnFailureListener(e -> {
            Log.d("FEEDBACK", "Error: " + e.toString());
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        });
    }

//    private void searchData(String input) {
//        idList.clear();
//        searchList.clear();
//        String appid = "HLDBOC7XRI";
//        String adminApiKey = "1a40eab368fd30c1ce3333a8e4658ca0";
//        Client client = new Client(appid, adminApiKey);
//        Index index = client.getIndex("pasien");
//        com.algolia.search.saas.Query query = new com.algolia.search.saas.Query(input)
//                .setAttributesToRetrieve("nama", "alamat", "telepon", "id")
//                .setHitsPerPage(50);
//        index.searchAsync(query, new CompletionHandler() {
//            @Override
//            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
//                try {
//                    JSONArray hits = jsonObject.getJSONArray("hits");
//                    Log.d("hits", hits.toString());
//                    for (int i = 0; i < hits.length(); i++) {
//                        JSONObject data = hits.getJSONObject(i);
//                        String nama = data.getString("nama");
//                        String id = data.getString("id");
//                        Log.d("nama", i + 1 + ". " + nama);
//                        //list.add(nama);
//                        idList.add(id);
//                    }
//                    showRecyclerView();
//                } catch (JSONException jsonException) {
//                    jsonException.printStackTrace();
//                }
//            }
//        });
//    }

    private void showRecyclerView() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}