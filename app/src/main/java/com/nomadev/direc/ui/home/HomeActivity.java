package com.nomadev.direc.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityHomeBinding;
import com.nomadev.direc.ui.home.byage.ByAgeFragment;
import com.nomadev.direc.ui.home.bycalendar.ByCalendarFragment;
import com.nomadev.direc.ui.home.bycalendar.DatePickAdapter;
import com.nomadev.direc.ui.home.byname.ByNameFragment;
import com.nomadev.direc.ui.home.dialogaddpasien.DialogAddPasienActivity;
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
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    MenuItem logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Menu navmenu = null;
        getMenuInflater().inflate(R.menu.navigation_menu,navmenu);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listDate = new ArrayList<>();
        adapterDate = new DatePickAdapter(listDate);
        setListCurrentDate();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        logOut = navmenu.findItem(R.id.nav_logout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            setListCurrentDate();
            initDateRangePicker();
            showDateRecyclerView();
            showCalendarLayout(true);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_home, ByCalendarFragment.class, null)
                    .commit();
        }

        logOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


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
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.date:
                Toast.makeText(this, "date selected", Toast.LENGTH_SHORT).show();
                initDateRangePicker();
                showDateRecyclerView();
                showCalendarLayout(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByCalendarFragment.class, null)
                        .commit();
                return true;
            case R.id.nama:
                Toast.makeText(this, "nama selected", Toast.LENGTH_SHORT).show();
                showCalendarLayout(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByNameFragment.class, null)
                        .commit();
                return true;
            case R.id.usia:
                Toast.makeText(this, "usia selected", Toast.LENGTH_SHORT).show();
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
            SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            c.add(Calendar.DATE, i);
            String date = formatDate.format(c.getTime());
            listDate.add(date);
        }
    }

    private void showDateRecyclerView() {
        binding.rvDate.setHasFixedSize(true);
        binding.rvDate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvDate.setAdapter(adapterDate);
    }

    private void showCalendarLayout(boolean state) {
        if (state) {
            binding.layoutCalendar.setVisibility(View.VISIBLE);
        } else {
            binding.layoutCalendar.setVisibility(View.GONE);
        }
    }
}