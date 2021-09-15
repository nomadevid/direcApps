package com.nomadev.direc.ui.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
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

import com.github.chrisbanes.photoview.PhotoView;
import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogFotoBinding;
import com.squareup.picasso.Picasso;

public class DialogFotoActivity extends DialogFragment {

    private ActivityDialogFotoBinding binding;
    private String url, nama, tanggal;
    private final String URL_FOTO = "url";
    private final String NAMA = "nama";
    private final String TANGGAL_PERIKSA = "tanggal_periksa";
    private int state = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogFotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        url = getArguments().getString(URL_FOTO);
        nama = getArguments().getString(NAMA);
        tanggal = getArguments().getString(TANGGAL_PERIKSA);
        Log.d("NAMA", "INI NAMA : "+ nama);

        binding.tvNama.setText(nama);
        binding.tvTanggal.setText(tanggal);
        PhotoView photoView = binding.pvFotoFull;
        Picasso.get().load(url).into(binding.pvFotoFull);

        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = (int) (getResources().getDisplayMetrics().heightPixels);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_foto);
        getDialog().show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = height;
        getDialog().getWindow().setAttributes(layoutParams);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.black));

        binding.pvFotoFull.setOnClickListener(v -> {
            state++;
            if (state % 2 != 0){
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

        binding.ibBack.setOnClickListener(v -> {
            getDialog().dismiss();
        });
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.direc_blue_light_background));
    }
}