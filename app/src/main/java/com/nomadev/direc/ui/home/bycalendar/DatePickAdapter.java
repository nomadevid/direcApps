package com.nomadev.direc.ui.home.bycalendar;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nomadev.direc.databinding.FragmentByCalendarBinding;
import com.nomadev.direc.databinding.ItemDateBinding;
import com.nomadev.direc.model.HistoryModel;

import java.util.ArrayList;
import java.util.List;

public class DatePickAdapter extends RecyclerView.Adapter<DatePickAdapter.ViewHolder> {

    private ArrayList<String> listDay = new ArrayList<>();
    private ArrayList<String> listDate = new ArrayList<>();

    public DatePickAdapter(ArrayList<String> listDay, ArrayList<String> listDate) {
        this.listDay = listDay;
        this.listDate = listDate;
    }

    @NonNull
    @Override
    public DatePickAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDateBinding binding = ItemDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        int height = parent.getMeasuredHeight();
        int width = parent.getMeasuredWidth() / getItemCount();
        Log.d("DatePickAdapter", String.valueOf(height));
        Log.d("DatePickAdapter", String.valueOf(width));
        binding.getRoot().setLayoutParams(new RecyclerView.LayoutParams(width, height));

        return new DatePickAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DatePickAdapter.ViewHolder holder, int position) {
        holder.bind(listDay.get(position), listDate.get(position));
    }

    @Override
    public int getItemCount() {
        return listDate.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemDateBinding binding;

        public ViewHolder(@NonNull ItemDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String hari, String tanggal) {

            binding.tvHari.setText(hari);
            binding.tvTanggal.setText(tanggal);
            binding.llBackground.setFocusable(true);
            binding.llBackground.setFocusableInTouchMode(true);
            binding.llBackground.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    binding.tvTanggal.setTextColor(Color.WHITE);
                } else {
                    binding.tvTanggal.setTextColor(Color.BLACK);
                }
            });

            binding.llBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
