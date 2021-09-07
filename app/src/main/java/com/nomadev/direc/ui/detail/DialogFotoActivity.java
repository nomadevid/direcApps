package com.nomadev.direc.ui.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
    private String url;
    private final String URL_FOTO = "url";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogFotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        url = getArguments().getString(URL_FOTO);

        PhotoView photoView = binding.pvFotoFull;
        Picasso.get().load(url).into(binding.pvFotoFull);

        int width = (int) (getResources().getDisplayMetrics().widthPixels);
        int height = (int) (getResources().getDisplayMetrics().heightPixels);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setContentView(R.layout.activity_dialog_foto);
        getDialog().show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = height;
        getDialog().getWindow().setAttributes(layoutParams);

        return view;
    }
}