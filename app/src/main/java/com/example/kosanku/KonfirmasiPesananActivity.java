package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class KonfirmasiPesananActivity extends AppCompatActivity {
    TextView namaorang, namakamar, nohp;
    TextView harga;
    TextView jenis;
    TextView kategori;
    TextView nomor;
    TextView checkin;
    TextView checkout;
    private SharedPreferences sharedPreferences, sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pesanan);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

         ImageView back = findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), KonfirmasiActivity.class);
                startActivity(intent);
                finish();
            }
        });

        namakamar = findViewById(R.id.namakamar);
        nomor = findViewById(R.id.nomor);
        checkin = findViewById(R.id.checkin);
        checkout = findViewById(R.id.checkout);
        namaorang = findViewById(R.id.nama);
        nohp = findViewById(R.id.nohp);

        sharedPreferences2 = getSharedPreferences("myPrefs", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("pesanan", MODE_PRIVATE);
        String namakamar2 = sharedPreferences.getString("nama", null);
        String nomor2 = sharedPreferences.getString("nomor", null);
        String checkin2 = sharedPreferences.getString("checkin", null);
        String checkout2 = sharedPreferences.getString("checkout", null);
        String namaorang2 = sharedPreferences2.getString("nama", null);
        String nohp2 = sharedPreferences2.getString("nohp", null);

        namaorang.setText(String.valueOf(namaorang2));
        nohp.setText(String.valueOf(nohp2));
        namakamar.setText(String.valueOf(namakamar2));
        nomor.setText(String.valueOf(nomor2));
        checkin.setText(String.valueOf(checkin2));
        checkout.setText(String.valueOf(checkout2));
    }
}