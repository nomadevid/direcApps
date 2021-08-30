package com.nomadev.direc.ui.home.byname;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.databinding.FragmentByNameBinding;
import com.nomadev.direc.model.PasienModel;

import java.util.ArrayList;
import java.util.List;

public class  ByNameFragment extends Fragment {
    private FragmentByNameBinding binding;
    private FirebaseFirestore db;
    private ArrayList<PasienModel> listPasien;
    private ByNameAdapter adapter;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentByNameBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        showProgressBar(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        listPasien = new ArrayList<>();

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listPasien.clear();
                        Log.d("listPasien: ", listPasien.toString());
                        getPasienData();
                        showRecyclerView();
                    }
                }, 2000);
            }
        });

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
                    id = pasienModel.setId(d.getId());
                    listPasien.add(pasienModel);
                }
                adapter.notifyDataSetChanged();
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                Toast.makeText(getActivity(), "Berhasil Mengambil Data.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
                Toast.makeText(getActivity(), "Data Kosong.", Toast.LENGTH_SHORT).show();
            }
            binding.refreshLayout.setRefreshing(false);
        }).addOnFailureListener(e -> {
            showProgressBar(false);
            Log.d("FEEDBACK", "Error: " + e.toString());
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            binding.refreshLayout.setRefreshing(false);
        });
    }

    private void showRecyclerView() {
        adapter = new ByNameAdapter(listPasien);
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
}