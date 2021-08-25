package com.nomadev.direc.ui.search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;

import com.nomadev.direc.databinding.ActivitySearchBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ArrayList<String> list;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();
        adapter = new SearchAdapter(list);

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchData(binding.etSearch.getText().toString());
            }
            return false;
        });
    }

    private void searchData(String input) {
        list.clear();
        String appid = "RUE1XIKN0I";
        String adminApiKey = "df48fa4680030c0e87c123242c291e69";
        Client client = new Client(appid, adminApiKey);
        Index index = client.getIndex("pasien");
        com.algolia.search.saas.Query query = new com.algolia.search.saas.Query(input)
                .setAttributesToRetrieve("nama", "alamat", "telepon")
                .setHitsPerPage(50);
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    JSONArray hits = jsonObject.getJSONArray("hits");
                    Log.d("hits", hits.toString());
                    for (int i = 0; i < hits.length(); i++) {
                        JSONObject data = hits.getJSONObject(i);
                        String nama = data.getString("nama");
                        Log.d("nama", i + 1 + ". " + nama);
                        list.add(nama);
                    }
                    showRecyclerView();
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        });
    }

    private void showRecyclerView() {
        binding.rvSearch.setHasFixedSize(true);
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearch.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}