package com.example.autobot1.activities.landing.frags;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.autobot1.activities.mechanics.viewmodels.BookingsViewModel;
import com.example.autobot1.databinding.ScheduleItemLayoutBinding;
import com.example.autobot1.models.ScheduleItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ScheduleItemLayout extends AppCompatActivity {
    private ScheduleItemLayoutBinding binding;
    private BookingsViewModel viewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Autobot");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        viewModel = new ViewModelProvider(this).get(BookingsViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ScheduleItemLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.submitSchedule.setOnClickListener(view1 -> {
            String title = Objects.requireNonNull(binding.scheduleTitleEt.getText()).toString().trim();
            String location = Objects.requireNonNull(binding.scheduleLocationEt.getText()).toString().trim();
            String description = Objects.requireNonNull(binding.descriptionScheduleEt.getText()).toString().trim();
            if (title.isEmpty()) {
                binding.scheduleTitleEt.setError("Cannot be empty");
            } else {
                if (location.isEmpty()) {
                    binding.scheduleLocationEt.setError("Cannot be empty");
                } else {
                    if (description.isEmpty()) {
                        binding.descriptionScheduleEt.setError("Cannot be empty");
                    } else {
                        ScheduleItem scheduleItem = new ScheduleItem(title, location, description);
                        viewModel.addScheduleMechanic(scheduleItem, FirebaseAuth.getInstance().getUid());
                    }
                }
            }
        });
    }
}
