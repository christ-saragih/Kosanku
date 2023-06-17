package com.example.kosanku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private LinearLayout editprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        editprofile = findViewById(R.id.editprofile);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            }
        });

        LinearLayout editpassword = findViewById(R.id.editpassword);
        editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditPasswordActivity.class));
            }
        });

        LinearLayout order = findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PemesananActivity.class));
            }
        });

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        if (sharedPreferences.contains("token")) {
            String token = sharedPreferences.getString("token", null);
            String nama = sharedPreferences.getString("nama", null);
            String email = sharedPreferences.getString("email", null);
            String avatar = sharedPreferences.getString("avatar", null);

            ImageView avatarImageView = findViewById(R.id.avatar);
            Picasso.get()
                    .load(avatar)
                    .into(avatarImageView);

            TextView nama_tv = findViewById(R.id.tv_nama);
            TextView email_tv = findViewById(R.id.tv_email);
            nama_tv.setText(String.valueOf(nama));
            email_tv.setText(String.valueOf(email));
        }
        ImageView logoutButton = findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Konfirmasi Logout")
                        .setMessage("Apakah Anda yakin ingin keluar?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Menghapus token dari SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("token");
                                editor.remove("nama");
                                editor.remove("username");
                                editor.remove("nohp");
                                editor.remove("email");
                                editor.remove("alamat");
                                editor.remove("avatar");
                                editor.remove("expiresIn");
                                editor.apply();

                                // Kembali ke halaman login
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
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
                        startActivity(new Intent(getApplicationContext(), KamarActivity.class) .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottom_pemesanan:
                        startActivity(new Intent(getApplicationContext(), PemesananActivity.class) .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottom_profile:
                        return true;
                }

                return false;
            }
        });


    }
    // Method untuk menampilkan Snackbar
    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorSnackbarBackground));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorSnackbarText));
        snackbar.show();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        overridePendingTransition(0, 0);
        finish();
    }

}