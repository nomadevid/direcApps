package com.nomadev.direc.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityHomeBinding;
import com.nomadev.direc.ui.home.byage.ByAgeFragment;
import com.nomadev.direc.ui.home.bycalendar.ByCalendarFragment;
import com.nomadev.direc.ui.home.byname.ByNameFragment;
import com.nomadev.direc.ui.home.dialogaddpasien.DialogAddPasienActivity;
import com.nomadev.direc.ui.login.LoginActivity;
import com.nomadev.direc.ui.search.SearchActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
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
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_home, ByNameFragment.class, null)
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
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByCalendarFragment.class, null)
                        .commit();
                return true;
            case R.id.nama:
                Toast.makeText(this, "nama selected", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_home, ByNameFragment.class, null)
                        .commit();
                return true;
            case R.id.usia:
                Toast.makeText(this, "usia selected", Toast.LENGTH_SHORT).show();
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
}