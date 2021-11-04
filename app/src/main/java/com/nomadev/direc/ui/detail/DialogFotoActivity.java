package com.nomadev.direc.ui.detail;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogFotoBinding;
import com.squareup.picasso.Picasso;

public class DialogFotoActivity extends DialogFragment {

    private ActivityDialogFotoBinding binding;
    private String url, nama, tanggal;
    private int state = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogFotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        if (getArguments() != null) {
            url = getArguments().getString(FotoStreamAdapter.ViewHolder.URL_FOTO);
            nama = getArguments().getString(FotoStreamAdapter.ViewHolder.NAMA);
            tanggal = getArguments().getString(FotoStreamAdapter.ViewHolder.TANGGAL_PERIKSA);
            Log.d("NAMA", "INI NAMA : " + nama);
        }

        binding.tvNama.setText(nama);
        binding.tvTanggal.setText(tanggal);
        Picasso.get().load(url).into(binding.pvFotoFull);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setContentView(R.layout.activity_dialog_foto);
            getDialog().show();
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = height;
        getDialog().getWindow().setAttributes(layoutParams);

        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.black));
        }

        binding.pvFotoFull.setOnClickListener(v -> {
            state++;
            if (state % 2 != 0) {
                binding.ibBack.setVisibility(View.GONE);
                binding.rlActionBar.setVisibility(View.GONE);
                binding.tvNama.setVisibility(View.GONE);
                binding.tvTanggal.setVisibility(View.GONE);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                binding.ibBack.setVisibility(View.VISIBLE);
                binding.rlActionBar.setVisibility(View.VISIBLE);
                binding.tvNama.setVisibility(View.VISIBLE);
                binding.tvTanggal.setVisibility(View.VISIBLE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });

        binding.ibBack.setOnClickListener(v -> getDialog().dismiss());
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.direc_blue_light_background));
        }
    }
}