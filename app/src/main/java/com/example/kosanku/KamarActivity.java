package com.example.kosanku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.makeramen.roundedimageview.RoundedImageView;

public class KamarActivity extends AppCompatActivity {
    private RoundedImageView iv_gambar_pria, iv_gambar_perempuan, iv_gambar_semuanya, iv_gambar_campuran;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamar);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        iv_gambar_pria = findViewById(R.id.iv_gambar_pria);
        iv_gambar_perempuan = findViewById(R.id.iv_gambar_perempuan);
        iv_gambar_campuran = findViewById(R.id.iv_gambar_campuran);
        iv_gambar_semuanya = findViewById(R.id.iv_gambar_semuanya);

        iv_gambar_pria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/pria/tersedia";
                Intent intent = new Intent(getApplicationContext(), KamarKategoriActivity.class);
                intent.putExtra("url", url);

                intent.putExtra("cek", "false");
                startActivity(intent);
            }
        });

        iv_gambar_perempuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/perempuan/tersedia";
                Intent intent = new Intent(getApplicationContext(), KamarKategoriActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("cek", "false");
                startActivity(intent);
            }
        });

        iv_gambar_campuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/campuran/tersedia";
                Intent intent = new Intent(getApplicationContext(), KamarKategoriActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("cek", "false");
                startActivity(intent);
            }
        });

        iv_gambar_semuanya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/tersedia";
                Intent intent = new Intent(getApplicationContext(), KamarKategoriActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("cek", "true");
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_kamar);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.bottom_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class) .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottom_kamar:
                        return true;
                    case R.id.bottom_pemesanan:
                        startActivity(new Intent(getApplicationContext(), PemesananActivity.class) .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottom_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class) .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }

                return false;
            }
        });

    }

    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        overridePendingTransition(0, 0);
        finish();
    }
}