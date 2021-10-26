package com.nomadev.direc.ui.detail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nomadev.direc.R;
import com.nomadev.direc.databinding.ActivityDialogDeleteDataActiivityBinding;

public class DialogExport extends DialogFragment {

    private ActivityDialogDeleteDataActiivityBinding binding;
    private DialogExportListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityDialogDeleteDataActiivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setContentView(R.layout.activity_dialog_delete_data_actiivity);
            getDialog().show();
        }

        setupDialog();

        return view;
    }

    private void setupDialog() {
        binding.tvHeaderAlertDialog.setText(R.string.export_rekam_data);
        binding.tvTextAlertDialog.setText(R.string.export_rekam_data_info);
        binding.btnHapus.setText(R.string.export);
        binding.btnHapus.setOnClickListener(v -> {
            listener.onExport(true);
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        });
        binding.btnTidak.setText(R.string.cancel);
        binding.btnTidak.setOnClickListener(v -> {
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DialogExportListener) context;
    }

    public interface DialogExportListener {
        void onExport(Boolean state);
    }
}
