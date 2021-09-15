package com.nomadev.direc.ui.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityHomeBinding;
import com.nomadev.direc.ui.home.byage.ByAgeFragment;
import com.nomadev.direc.ui.home.bycalendar.ByCalendarFragment;
import com.nomadev.direc.ui.home.bycalendar.DatePickAdapter;
import com.nomadev.direc.ui.home.byname.ByNameFragment;
import com.nomadev.direc.ui.home.dialogaddpasien.DialogAddPasienActivity;
import com.nomadev.direc.ui.home.laporerror.LaporErrorActivity;
import com.nomadev.direc.ui.login.LoginActivity;
import com.nomadev.direc.ui.search.SearchActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private DatePickerDialog datePickerDialog;
    private ArrayList<String> listDate;
    private DatePickAdapter adapterDate;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listDate = new ArrayList<>();
        adapterDate = new DatePickAdapter(listDate);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.myDrawerLayout, R.string.nav_open, R.string.nav_close);
        binding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        initDateRangePicker();
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            setListCurrentDate();
            showCalendarLayout(true);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_home, ByCalendarFragment.class, null)
                    .commit();
        }

        binding.ibFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(HomeActivity.this, v);
            popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
            popup.inflate(R.menu.filter_menu);
            popup.show();
        });

        binding.ibSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        binding.floatingActionButton.setOnClickListener(v -> {
            DialogAddPasienActivity dialog = new DialogAddPasienActivity();
            dialog.show(getSupportFragmentManager(), "DialogAddPasien");
        });

        binding.ibCalendar.setOnClickListener(v -> datePickerDialog.show());

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.laporError.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LaporErrorActivity.class);
            startActivity(intent);
        });

        binding.btnHamburger.setOnClickListener(v -> binding.myDrawerLayout.openDrawer(GravityCompat.START));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.date:
                showDateRecyclerView();
                showCalendarLayout(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByCalendarFragment.class, null)
                        .commit();
                return true;
            case R.id.nama:
                showCalendarLayout(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByNameFragment.class, null)
                        .commit();
                return true;
            case R.id.usia:
                showCalendarLayout(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByAgeFragment.class, null)
                        .commit();
                return true;
            default:
                return false;
        }
    }

    private void initDateRangePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            listDate.clear();
            Calendar cld = Calendar.getInstance();
            for (int i = 0; i < 6; i++) {
                cld.set(year, month, dayOfMonth);
                cld.add(Calendar.DATE, i);
                SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String date = formatDate.format(cld.getTime());
                listDate.add(date);
            }
            adapterDate.notifyDataSetChanged();
            Log.d("listDate", listDate.toString());
            showDateRecyclerView();
        };
        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, dateSetListener, tahun, bulan, hari);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_box_white);
    }

    private void setListCurrentDate() {
        listDate.clear();
        for (int i = 0; i < 6; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, i);
            SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String date = formatDate.format(c.getTime());
            listDate.add(date);
        }
        adapterDate.notifyDataSetChanged();
        Log.d("listDate", listDate.toString());
        showDateRecyclerView();
    }

    private void showDateRecyclerView() {
        binding.rvDate.setHasFixedSize(true);
        binding.rvDate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvDate.setAdapter(adapterDate);
    }

    private void showCalendarLayout(boolean state) {
        if (state) {
            binding.rvDate.setAlpha(0f);
            binding.ibCalendar.setAlpha(0f);

            binding.rvDate.setVisibility(View.VISIBLE);
            binding.ibCalendar.setVisibility(View.VISIBLE);

            binding.rvDate.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);

            binding.ibCalendar.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);
        } else {
            binding.rvDate.animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            binding.rvDate.setVisibility(View.GONE);
                        }
                    });

            binding.ibCalendar.animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            binding.ibCalendar.setVisibility(View.GONE);
                        }
                    });
        }
    }
}