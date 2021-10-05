package com.nomadev.direc.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.nomadev.direc.BuildConfig;
import com.nomadev.direc.databinding.ActivitySearchBinding;
import com.nomadev.direc.model.PasienModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ArrayList<PasienModel> searchList;
    private ArrayList<String> idList;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchList = new ArrayList<>();
        idList = new ArrayList<>();
        adapter = new SearchAdapter(searchList, idList);
        showProgressBar(false);

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchAlgolia(binding.etSearch.getText().toString());
//                getSearchData(binding.etSearch.getText().toString());
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
                searchAlgolia(s.toString());
            }
        });
    }

    private void searchAlgolia(String input) {
        Client client = new Client(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_ADMIN_API_KEY);
        Index index = client.getIndex("pasien");
        com.algolia.search.saas.Query query = new com.algolia.search.saas.Query(input)
                .setAttributesToRetrieve("nama", "alamat", "telepon")
                .setHitsPerPage(50);
        index.searchAsync(query, (jsonObject, e) -> {
            try {
                idList.clear();
                searchList.clear();
                showProgressBar(false);
                JSONArray hits;
                if (jsonObject != null) {
                    hits = jsonObject.getJSONArray("hits");
                    if (hits.length() > 0) {
                        Log.d("hits", hits.toString());
                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject data = hits.getJSONObject(i);
                            String id = data.getString("objectID");
                            String nama = data.getString("nama");
                            String telepon = data.getString("telepon");
                            String alamat = data.getString("alamat");
                            Log.d("nama", i + 1 + ". " + nama);

                            PasienModel pasienModel = new PasienModel(nama, 0, telepon, alamat, "", false);
                            searchList.add(pasienModel);
                            idList.add(id);
                        }
                        Log.d("idList", idList.toString());
                        Log.d("searchList", searchList.toString());
                        showRecyclerView();
                        showSearchInfo(false);
                    } else {
                        showSearchInfo(true);
                        binding.rvSearch.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
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