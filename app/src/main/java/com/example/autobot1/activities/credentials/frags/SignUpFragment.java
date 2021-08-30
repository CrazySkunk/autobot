package com.example.autobot1.activities.credentials.frags;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.MapActivity;
import com.example.autobot1.databinding.FragmentSignUpBinding;
import com.example.autobot1.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Objects;


public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";
    private FragmentSignUpBinding binding;
    private Animation animation;
    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.explosion_animation);
        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginPageButton.setOnClickListener(loginFab -> {
            View animationView = binding.animationView;
            animationView.setVisibility(View.VISIBLE);
            animationView.setAnimation(animation);
            animation.start();
            NavHostFragment.findNavController(SignUpFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });
        binding.profilePicIv.setOnClickListener(v -> {
            if (canReadStorage()) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 200);
            } else {
                requireActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        });
        binding.signUpButton.setOnClickListener(signUpBtn -> {
            String accountType;
            String name = Objects.requireNonNull(binding.nameInputEt.getText()).toString().trim();
            String email = Objects.requireNonNull(binding.emailInputEt.getText()).toString().trim();
            String phoneNo = Objects.requireNonNull(binding.phoneInputEt.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.passwordInputEt.getText()).toString().trim();
            int selectedAccount = binding.radioGroup.getCheckedRadioButtonId();
            if (selectedAccount == R.id.mechanic_radio) {
                accountType = "Mechanic";
            } else {
                accountType = "Client";
            }
            if (name.isEmpty()) {
                binding.nameInputEt.setError("Cannot be empty");
            } else {
                if (email.isEmpty()) {
                    binding.emailInputLayout.setError("Cannot be empty");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.emailInputLayout.setError("Invalid email address");
                    } else {
                        if (phoneNo.isEmpty()) {
                            binding.phoneInputLayout.setError("Cannot be empty");
                        } else {
                            if (Patterns.PHONE.matcher(phoneNo).matches()) {
                                binding.phoneInputLayout.setError("Invalid phone number");
                            } else {
                                if (password.isEmpty()) {
                                    binding.passwordInputEt.setError("Cannot be empty");
                                } else {
                                    if (password.length() < 8) {
                                        binding.passwordInputEt.setError("Try 8 character password");
                                    } else {
                                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        addImageToStorage(imageUri, name, email, phoneNo, accountType);
                                                    }
                                                });
                                    }
                                }
                            }
                        }

                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canReadStorage() {
        return requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private void addImageToStorage(Uri imageUri, String name, String email, String phoneNo,String accountType) {
        StorageReference reference = FirebaseStorage.getInstance().getReference("user-images/" + FirebaseAuth.getInstance().getUid());
        reference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    String downloadUrl = reference.getDownloadUrl().toString();
                    addUserToDb(name, email, downloadUrl, phoneNo, accountType);
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && Arrays.equals(permissions, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 200);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                binding.profilePicIv.setImageBitmap(bm);
            } catch (Exception e) {
                Log.i(TAG, "onActivityResult: Exception " + e.getMessage());
            }
        }
    }

    private void addUserToDb(String name, String email, String downloadUrl, String phoneNo, String accountType) {
        User user = new User(FirebaseAuth.getInstance().getUid(), name, downloadUrl, email, phoneNo, accountType);
        FirebaseFirestore.getInstance().collection("users/" + accountType)
                .add(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        requireContext().startActivity(new Intent(requireContext(), MapActivity.class));
                    }
                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Something went wrong try again", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}