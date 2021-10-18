package com.nomadev.direc.ui.home.bycalendar;

import android.os.Bundle;
import android.os.Handler;
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
import com.nomadev.direc.databinding.FragmentByCalendarBinding;
import com.nomadev.direc.model.HistoryModel;

import java.util.ArrayList;
import java.util.List;

public class ByCalendarFragment extends Fragment {

    private FragmentByCalendarBinding binding;
    private FirebaseFirestore db;
    private ArrayList<HistoryModel> listHistory;
    private ByCalendarAdapter adapter;
    private String date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentByCalendarBinding.inflate(inflater, container, false);
        showProgressBar(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init
        db = FirebaseFirestore.getInstance();
        listHistory = new ArrayList<>();
        adapter = new ByCalendarAdapter(listHistory);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date");
            Log.d("ByCalendarFragment", date);
            showProgressBar(true);
            getHistoryData();
            showRecyclerView();
        }

        binding.refreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            listHistory.clear();
            getHistoryData();
            showRecyclerView();
        }, 2000));
    }

    private void getHistoryData() {
        CollectionReference dbPasien = db.collection("history_pasien_all");
        Query query = dbPasien.whereEqualTo("addDate", date)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(100);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            showProgressBar(false);
            Log.d("queryDocumentSnapshots", queryDocumentSnapshots.toString());
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    Log.d("SNAPSHOT", d.toString());
                    HistoryModel historyModel = d.toObject(HistoryModel.class);
                    listHistory.add(historyModel);
                }
                adapter.notifyDataSetChanged();
                showInfo(false);
                binding.rvByCalendar.setVisibility(View.VISIBLE);
            } else {
                showInfo(true);
                binding.rvByCalendar.setVisibility(View.GONE);
            }
            binding.refreshLayout.setRefreshing(false);
        }).addOnFailureListener(e -> {
            showProgressBar(false);
            Log.d("FEEDBACK", "Error: " + e.toString());
            binding.refreshLayout.setRefreshing(false);
        });
    }

    private void showRecyclerView() {
        binding.rvByCalendar.setHasFixedSize(true);
        binding.rvByCalendar.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvByCalendar.setAdapter(adapter);
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