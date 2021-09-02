package com.nomadev.direc.ui.home.bycalendar;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ItemByCalendarBinding;
import com.nomadev.direc.model.HistoryModel;
import com.nomadev.direc.ui.detail.DetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ByCalendarAdapter extends RecyclerView.Adapter<ByCalendarAdapter.ViewHolder> {

    private final ArrayList<HistoryModel> listHistory;

    public ByCalendarAdapter(ArrayList<HistoryModel> listHistory) {
        this.listHistory = listHistory;
    }

    @NonNull
    @Override
    public ByCalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ByCalendarAdapter.ViewHolder(ItemByCalendarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ByCalendarAdapter.ViewHolder holder, int position) {
        holder.bind(listHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemByCalendarBinding binding;

        public ViewHolder(@NonNull ItemByCalendarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HistoryModel data) {
            String id = data.getIdPasien();

            binding.tvTime.setText(timeParse(data.getAddTime()));
            binding.tvNama.setText(data.getNama());
            binding.tvUsia.setText(itemView.getContext().getString(R.string.usia_terisi, calculateAge(data.getTanggalLahir())));
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.ID, id);
                itemView.getContext().startActivity(intent);
            });
        }

        private String calculateAge(String tanggalLahir) {
            // KONVERSI STRING KE DATE
            String ageString = "";
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date date = format.parse(tanggalLahir);

                // HITUNG USIA
                Calendar dob = Calendar.getInstance();
                Calendar today = Calendar.getInstance();

                if (date != null) {
                    dob.setTime(date);

                    int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                        age--;
                    }

                    ageString = String.valueOf(age);
                    Log.d("usia", ageString);
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }

            return ageString;
        }

        private String timeParse(String addTime) {
            String parsedTime = "";

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            try {
                Date date = format.parse(addTime);
                SimpleDateFormat newFormat = new SimpleDateFormat("HH.mm", Locale.getDefault());
                if (date != null) {
                    parsedTime = newFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return parsedTime;
        }
    }
}
