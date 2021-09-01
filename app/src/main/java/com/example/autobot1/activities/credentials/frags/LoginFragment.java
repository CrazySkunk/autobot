package com.example.autobot1.activities.credentials.frags;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.MapActivity;
import com.example.autobot1.activities.mechanics.MechanicsActivity;
import com.example.autobot1.databinding.FragmentLoginBinding;
import com.example.autobot1.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
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
                                        String uid = FirebaseAuth.getInstance().getUid();
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        snapshot.getChildren().forEach(user->{
                                                            User u = user.getValue(User.class);
                                                            if (u!=null){
                                                                if (u.getUid().equals(uid)){
                                                                    if (u.getAccountType().equals("Mechanic")){
                                                                        requireActivity().startActivity(new Intent(requireContext(), MechanicsActivity.class));
                                                                    }else {
                                                                        requireActivity().startActivity(new Intent(requireContext(),MapActivity.class));
                                                                    }
                                                                    progressDialog.dismiss();
                                                                    requireActivity().finish();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.i(TAG, "onCancelled: error -> "+error.getMessage());
                                                    }
                                                });
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