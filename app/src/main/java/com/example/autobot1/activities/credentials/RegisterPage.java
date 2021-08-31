package com.example.autobot1.activities.credentials;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autobot1.R;
import com.example.autobot1.activities.landing.MapActivity;
import com.example.autobot1.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RegisterPage extends AppCompatActivity {
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                this::onSignInResults
        );

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }
    @SuppressLint("RestrictedApi")
    private void onSignInResults(FirebaseAuthUIAuthenticationResult result){
        IdpResponse response = result.getIdpResponse();
        if(result.getResultCode()==RESULT_OK){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert response != null;
            if (response.isNewUser()){
                String name = response.getUser().getName();
                String email = response.getUser().getEmail();
                String phone = response.getUser().getPhoneNumber();
                String imageUri = Objects.requireNonNull(response.getUser().getPhotoUri()).toString();
                View view = LayoutInflater.from(this).inflate(R.layout.account_type_dialog,null,false);
                RadioGroup radioGroup = view.findViewById(R.id.account_type_radio_alert);
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Select account type")
                        .setView(view)
                        .setPositiveButton("Submit", (dialog, which) -> {
                            if (radioGroup.getCheckedRadioButtonId()==R.id.client_radio_alert){
                                accountType = "Client";
                            }else {
                                accountType = "Mechanic";
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                assert user != null;
                addUserToDB(user.getUid(),name,email,imageUri,phone,accountType);
            }
        }else {
            Toast.makeText(this,"Something went wrong try again",Toast.LENGTH_SHORT).show();
        }
    }
    private void addUserToDB(String uid,String name,String email,String imageUri,String phone,String accountType){
        User user = new User(uid,name,email,imageUri,phone,accountType);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/"+uid);
        reference.setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isComplete()){
                        Toast.makeText(RegisterPage.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RegisterPage.this, "Welcome....", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MapActivity.class));
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,CredentialsActivity.class));
        finish();
    }
}