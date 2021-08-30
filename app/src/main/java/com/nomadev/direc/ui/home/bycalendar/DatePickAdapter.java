package com.nomadev.direc.ui.home.bycalendar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ItemDateBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatePickAdapter extends RecyclerView.Adapter<DatePickAdapter.ViewHolder> {

    private ArrayList<String> listDate = new ArrayList<>();

    public DatePickAdapter(ArrayList<String> listDate) {
        this.listDate = listDate;
    }

    @NonNull
    @Override
    public DatePickAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDateBinding binding = ItemDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        int wrapSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        binding.getRoot().measure(wrapSpec, wrapSpec);

        int height = binding.getRoot().getMeasuredHeight();
        int width = parent.getMeasuredWidth() / getItemCount();
        Log.d("DatePickAdapterHeight", String.valueOf(height));
        Log.d("DatePickAdapterWidth", String.valueOf(width));
        binding.getRoot().setLayoutParams(new RecyclerView.LayoutParams(width, height));

        return new DatePickAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DatePickAdapter.ViewHolder holder, int position) {
        holder.bind(listDate.get(position));
    }

    @Override
    public int getItemCount() {
        return listDate.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemDateBinding binding;
        private String dayString, dateString;

        public ViewHolder(@NonNull ItemDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String tanggal) {
            parseDate(tanggal);
            binding.tvHari.setText(dayString);
            binding.tvTanggal.setText(dateString);
            binding.llBackground.setFocusable(true);
            binding.llBackground.setFocusableInTouchMode(true);
            binding.llBackground.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    Log.d("DatePickAdapterDate", tanggal);
                    openFragment(v, tanggal);
                    binding.tvHari.setTextColor(Color.WHITE);
                    binding.tvTanggal.setTextColor(Color.WHITE);
                } else {
                    binding.tvHari.setTextColor(itemView.getResources().getColor(R.color.direc_grey));
                    binding.tvTanggal.setTextColor(Color.BLACK);
                }
            });
        }

        private void openFragment(View v, String tanggal) {
            Log.d("DatePickAdapterTanggal", tanggal);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            Fragment myFragment = new ByCalendarFragment();

            Bundle bundle = new Bundle();
            bundle.putString("date", tanggal);
            myFragment.setArguments(bundle);

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_home, myFragment, null)
                    .commit();
        }

        private void parseDate(String tanggal) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                Date date = format.parse(tanggal);
                SimpleDateFormat formatDay = new SimpleDateFormat("EEE", Locale.getDefault());
                SimpleDateFormat formatDate = new SimpleDateFormat("dd", Locale.getDefault());
                if (date != null) {
                    dayString = formatDay.format(date);
                    dateString = formatDate.format(date);
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        }
    }
}
