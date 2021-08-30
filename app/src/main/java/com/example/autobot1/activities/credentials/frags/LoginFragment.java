package com.example.autobot1.activities.credentials.frags;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.MapActivity;
import com.example.autobot1.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private Animation animation;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Autobot");
        progressDialog.setMessage("Logging in...");
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.explosion_animation);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(500);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.signUpPageButton.setOnClickListener(signUpFab -> {
            View animationView = binding.animationView;
            animationView.setVisibility(View.VISIBLE);
            animationView.setAnimation(animation);
            animation.start();
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });
        binding.loginButton.setOnClickListener(loginBtn -> {
            progressDialog.show();
            String email = Objects.requireNonNull(binding.emailInputLoginEt.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.passwordInputLoginEt.getText()).toString().trim();
            if (email.isEmpty()) {
                progressDialog.dismiss();
                binding.emailInputLayout.setError("Cannot be empty");
            } else {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    progressDialog.dismiss();
                    binding.emailInputLayout.setError("Invalid email address");
                } else {
                    if (password.isEmpty()) {
                        progressDialog.dismiss();
                        binding.passwordInputLayout.setError("Cannot be empty");
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        requireContext().startActivity(new Intent(requireContext(), MapActivity.class));
                                    }
                                });
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}