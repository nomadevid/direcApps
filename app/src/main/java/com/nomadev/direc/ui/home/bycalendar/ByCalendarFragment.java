package com.nomadev.direc.ui.home.bycalendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.FragmentByCalendarBinding;
import com.nomadev.direc.model.HistoryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ByCalendarFragment extends Fragment {

    private FragmentByCalendarBinding binding;
    private FirebaseFirestore db;
    private ArrayList<HistoryModel> listHistory;
    private ArrayList<String> listDate;
    private ArrayList<String> listDay;
    private ByCalendarAdapter adapter;
    private DatePickAdapter adapterDate;
    private DatePickerDialog datePickerDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentByCalendarBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init
        db = FirebaseFirestore.getInstance();
        listHistory = new ArrayList<>();
        listDate = new ArrayList<>();
        listDay = new ArrayList<>();
        adapter = new ByCalendarAdapter(listHistory);
        adapterDate = new DatePickAdapter(listDay, listDate);
        initDateRangePicker();

        getHistoryData();
        showRecyclerView();
        showDateRecyclerView();

        binding.ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    private void getHistoryData() {
        CollectionReference dbPasien = db.collection("history_pasien");
        Query query = dbPasien.orderBy("addTime", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("queryDocumentSnapshots", queryDocumentSnapshots.toString());
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    Log.d("SNAPSHOT", d.toString());
                    HistoryModel historyModel = d.toObject(HistoryModel.class);
                    listHistory.add(historyModel);
                }
                adapter.notifyDataSetChanged();
                Log.d("FEEDBACK", "Berhasil Mengambil Data.");
                Toast.makeText(getActivity(), "Berhasil Mengambil Data.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("FEEDBACK", "Data Kosong.");
                Toast.makeText(getActivity(), "Data Kosong.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.d("FEEDBACK", "Error: " + e.toString());
            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private void initDateRangePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                listDay.clear();
                listDate.clear();
                Calendar cld = Calendar.getInstance();
                for (int i = 0; i < 6; i++) {
                    cld.set(year, month, dayOfMonth);
                    cld.add(Calendar.DATE, i);
                    SimpleDateFormat formatDay = new SimpleDateFormat("EEE", Locale.getDefault());
                    SimpleDateFormat formatDate = new SimpleDateFormat("dd", Locale.getDefault());
                    String day = formatDay.format(cld.getTime());
                    String date = formatDate.format(cld.getTime());
                    listDay.add(day);
                    listDate.add(date);
                }
                Log.d("listDate", listDay.toString() + listDate.toString());
                showDateRecyclerView();
            }
        };
        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, dateSetListener, tahun, bulan, hari);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);

    }

    private void showRecyclerView() {
        binding.rvByCalendar.setHasFixedSize(true);
        binding.rvByCalendar.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvByCalendar.setAdapter(adapter);
    }

    private void showDateRecyclerView() {
        binding.rvDate.setHasFixedSize(true);
        binding.rvDate.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvDate.setAdapter(adapterDate);
    }
}