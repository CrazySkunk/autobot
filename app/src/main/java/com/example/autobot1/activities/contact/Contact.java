package com.example.autobot1.activities.contact;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autobot1.activities.contact.data.EmailContent;
import com.example.autobot1.databinding.ActivityContactBinding;
import com.example.autobot1.models.ShopItem;

public class Contact extends AppCompatActivity {
    private ActivityContactBinding binding;
    private ShopItem shopItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        shopItem = intent.getParcelableExtra("shop");
        binding.emailAddressEt.setText(shopItem.getContact());
        binding.sendEmailBtn.setOnClickListener(v -> sendEmail());
        binding.callBtn.setOnClickListener(v -> call());
    }

    private void sendEmail() {
        String subject = binding.subjectEt.getText().toString();
        String description = binding.descriptionEt.getText().toString();
        EmailContent emailContent = new EmailContent(shopItem.getContact(), subject, description);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("content", emailContent);
        startActivity(intent);
    }
    private void call(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.putExtra("number",shopItem.getContact());
        startActivity(intent);
    }
}

