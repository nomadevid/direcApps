package com.nomadev.direc.ui.home.bydiagnosa;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.FragmentByDiagnosaBinding;
import com.nomadev.direc.model.HistoryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ByDiagnosaFragment extends Fragment {

    private FragmentByDiagnosaBinding binding;
    private FirebaseFirestore db;
    private ArrayList<HistoryModel> listHistory;
    private ByDiagnosaAdapter adapter;
    private DatePickerDialog startDatePicker, endDatePicker;
    private Date startDate, endDate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentByDiagnosaBinding.inflate(inflater, container, false);
        showProgressBar(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 0);

        startDate = start.getTime();
        endDate = end.getTime();
        db = FirebaseFirestore.getInstance();
        listHistory = new ArrayList<>();
        adapter = new ByDiagnosaAdapter(listHistory);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        binding.refreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            listHistory.clear();
            getHistory(startDate, endDate);
            showRecyclerView();
        }, 2000));

        binding.btnStartDate.setText(df.format(startDate));
        binding.btnEndDate.setText(df.format(endDate));

        binding.btnStartDate.setOnClickListener(v -> startDatePicker.show());
        binding.btnEndDate.setOnClickListener(v -> endDatePicker.show());

        String[] penyakit = {getString(R.string.penyakit_katarak), getString(R.string.penyakit_pterygium), getString(R.string.penyakit_hordeolum), getString(R.string.penyakit_Uveitis), getString(R.string.penyakit_poag), getString(R.string.penyakit_pacg), getString(R.string.penyakit_kelainan_refraksi), getString(R.string.penyakit_lainnya)};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_text_style, penyakit);
        binding.spinnerKetegori.setAdapter(arrayAdapter);
        binding.spinnerKetegori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getHistory(startDate, endDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showProgressBar(true);
        initStartDatePicker(df);
        initEndDatePicker(df);
        getHistory(startDate, endDate);
        showRecyclerView();
    }

    private void initStartDatePicker(SimpleDateFormat df) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar cld = Calendar.getInstance();
            cld.set(year, month, dayOfMonth);
            cld.set(Calendar.HOUR_OF_DAY, 0);
            cld.set(Calendar.MINUTE, 0);
            cld.set(Calendar.SECOND, 0);
            cld.set(Calendar.MILLISECOND, 0);
            startDate = cld.getTime();
            binding.btnStartDate.setText(df.format(startDate));
            getHistory(startDate, endDate);
        };

        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        startDatePicker = new DatePickerDialog(getActivity(), R.style.DialogTheme, dateSetListener, tahun, bulan, hari);
        startDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        startDatePicker.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
    }

    private void initEndDatePicker(SimpleDateFormat df) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar cld = Calendar.getInstance();
            cld.set(year, month, dayOfMonth);
            cld.set(Calendar.HOUR_OF_DAY, 23);
            cld.set(Calendar.MINUTE, 59);
            cld.set(Calendar.SECOND, 59);
            cld.set(Calendar.MILLISECOND, 0);
            endDate = cld.getTime();
            binding.btnEndDate.setText(df.format(endDate));
            getHistory(startDate, endDate);
        };

        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        endDatePicker = new DatePickerDialog(getActivity(), R.style.DialogTheme, dateSetListener, tahun, bulan, hari);
        endDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        endDatePicker.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
    }

    private void getHistory(Date dateStart, Date dateEnd) {
        listHistory.clear();
        CollectionReference dbRef = db.collection("history_pasien_all");
        Query query = dbRef.whereEqualTo("penyakit", String.valueOf(binding.spinnerKetegori.getSelectedItemPosition()))
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .startAt(dateStart)
                .endAt(dateEnd)
                .limit(100);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            showProgressBar(false);
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    HistoryModel model = d.toObject(HistoryModel.class);
                    listHistory.add(model);
                }
                Log.d("history_list", listHistory.toString());
                adapter.notifyDataSetChanged();
                showInfo(false);
                binding.refreshLayout.setRefreshing(false);
                binding.tvBanyakData.setVisibility(View.VISIBLE);
                binding.tvBanyakData.setText("Terdapat " + listHistory.size() + " data untuk diagnosa ini.");
                binding.rv.setVisibility(View.VISIBLE);
            } else {
                showInfo(true);
                binding.tvBanyakData.setVisibility(View.GONE);
                binding.rv.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Log.d("FEEDBACK", "GAGAL: " + e.toString());
            showProgressBar(false);
            binding.refreshLayout.setRefreshing(false);
        });
    }

    private void showRecyclerView() {
        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rv.setAdapter(adapter);
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