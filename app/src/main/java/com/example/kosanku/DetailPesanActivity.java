package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailPesanActivity extends AppCompatActivity {


    TextView nama;
    TextView harga;
    TextView jenis;
    TextView kategori;
    TextView nomor;
    TextView checkin;
    TextView checkout;
    TextView durasi;
    TextView total;
    RoundedImageView gambar;

    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences, sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Button button_back = findViewById(R.id.cancel);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("pesanan", MODE_PRIVATE);
                String nomor = sharedPreferences.getString("nomor", "");
                Intent intent = new Intent(getApplicationContext(), DetailKamarActivity.class);
                intent.putExtra("nomor_kamar", nomor);
                startActivity(intent);
                finish();
            }
        });



        nama = findViewById(R.id.tv_nama);
        harga = findViewById(R.id.tv_harga);
        nomor = findViewById(R.id.tv_nomor);
        jenis = findViewById(R.id.tv_jenis);
        kategori = findViewById(R.id.tv_kategori);
        checkin = findViewById(R.id.tv_checkin);
        checkout = findViewById(R.id.tv_checkout);
        gambar = findViewById(R.id.iv_gambar);
        durasi = findViewById(R.id.tv_durasi);
        total = findViewById(R.id.tv_total);
        sharedPreferences2 = getSharedPreferences("myPrefs", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("pesanan", MODE_PRIVATE);
        String nama2 = sharedPreferences.getString("nama", null);
        String harga2 = sharedPreferences.getString("harga", null);
        String jenis2 = sharedPreferences.getString("jenis", null);
        String kategori2 = sharedPreferences.getString("kategori", null);
        String nomor2 = sharedPreferences.getString("nomor", null);
        String gambar2 = sharedPreferences.getString("gambar", null);
        String checkin2 = sharedPreferences.getString("checkin", null);
        String checkout2 = sharedPreferences.getString("checkout", null);



        nama.setText(String.valueOf(nama2));
        harga.setText(String.valueOf(harga2));
        nomor.setText(String.valueOf(nomor2));
        jenis.setText(String.valueOf(jenis2));
        kategori.setText(String.valueOf(kategori2));
        checkin.setText(String.valueOf(checkin2));
        checkout.setText(String.valueOf(checkout2));

        Picasso.get().load(gambar2).into(gambar);
        Picasso.get()
                .load(gambar2)
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        Bitmap transformedBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
                        Canvas canvas = new Canvas(transformedBitmap);
                        RoundedDrawable roundedDrawable = new RoundedDrawable(source);
                        roundedDrawable.setCornerRadius(getResources().getDimension(R.dimen.rounded_corner_radius));

                        roundedDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        roundedDrawable.draw(canvas);

                        if (transformedBitmap != source) {
                            source.recycle();
                        }
                        return transformedBitmap;
                    }

                    @Override
                    public String key() {
                        return "rounded";
                    }
                })
                .into(gambar);


        SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", new Locale("id"));
        String tanggal1 = checkin2;
        String tanggal2 = checkout2;
        try {
            Date date1 = format.parse(tanggal1);
            Date date2 = format.parse(tanggal2);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            int tahun1 = calendar1.get(Calendar.YEAR);
            int bulan1 = calendar1.get(Calendar.MONTH);

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(date2);
            int tahun2 = calendar2.get(Calendar.YEAR);
            int bulan2 = calendar2.get(Calendar.MONTH);

            int durasiTahun = tahun2 - tahun1;
            int durasiBulan = bulan2 - bulan1;
            int totals = 0;
            int totalDurasiBulan = durasiTahun * 12 + durasiBulan;
            if (totalDurasiBulan == 0) {
                totalDurasiBulan = 1;
                totals = totalDurasiBulan * Integer.parseInt(harga2);
                total.setText("Rp " + String.valueOf(totals));
                durasi.setText("1 Bulan");
            }else {
                totals = totalDurasiBulan * Integer.parseInt(harga2);
                total.setText("Rp " + String.valueOf(totals));
                durasi.setText(String.valueOf(totalDurasiBulan) + " Bulan");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Button button_pesan = findViewById(R.id.pesan);
        button_pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pesan(nomor2, checkin2, checkout2);
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Hapus semua data dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("pesanan", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        super.onBackPressed();
    }


    // Fungsi untuk menampilkan ProgressDialog
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(DetailPesanActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    // Fungsi untuk menyembunyikan ProgressDialog
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void executeRequest(String nomor2, String checkin2, String checkout2) {
        showProgressDialog();

        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/pemesanan";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("status");
                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");

                            if (success) {
                                Toast.makeText(DetailPesanActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DetailPesanActivity.this, KonfirmasiActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                hideProgressDialog();
                                Toast.makeText(DetailPesanActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String errorMessage = new String(networkResponse.data);
                            Log.e("error", errorMessage);
                            Toast.makeText(DetailPesanActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        } else {
                            Log.e("error", "onErrorResponse: " + error.getMessage(), error);
                            Toast.makeText(DetailPesanActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("no_kamar", nomor2);
                    jsonObject.put("tanggal_checkin", checkin2);
                    jsonObject.put("tanggal_checkout", checkout2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences2.getString("token", null);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DetailPesanActivity.this);
        requestQueue.add(stringRequest);
    }
    private void pesan(String nomor2, String checkin2, String checkout2 ) {

        executeRequest(nomor2, checkin2, checkout2);
    }
}
