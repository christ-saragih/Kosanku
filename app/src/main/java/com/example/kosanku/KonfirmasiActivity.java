package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KonfirmasiActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    Button konfirmasi, cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        konfirmasi = findViewById(R.id.konfirmasi);
        konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KonfirmasiActivity.this, KonfirmasiPesananActivity.class);
                startActivity(intent);
            }
        });
        cancel = findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("pesanan", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(KonfirmasiActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("pesanan", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        overridePendingTransition(0, 0);
        finish();
    }
}