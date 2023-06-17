package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailPemesananActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pemesanan);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sharedPreferences2 = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String namaorang2 = sharedPreferences2.getString("nama", null);
        String nohp2 = sharedPreferences2.getString("nohp", null);
        fetchDataFromAPI();
        ImageView button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PemesananActivity.class));
                finish();
            }
        });

        TextView textViewNamaOrang = findViewById(R.id.nama);
        textViewNamaOrang.setText(namaorang2);

        TextView textViewNohp = findViewById(R.id.nohp);
        textViewNohp.setText(nohp2);



    }
    private void fetchDataFromAPI() {
        String endpoint = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/pemesanan/detail";
        Uri.Builder builder = Uri.parse(endpoint).buildUpon();
        Intent intent = getIntent();
        String nomorKamar = intent.getStringExtra("nomor_kamar");
        builder.appendQueryParameter("nomor", nomorKamar);
        String url = builder.build().toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing data JSON
                            String namaKamar = response.getString("nama_kamar");
                            String nomorKamar = String.valueOf(response.getInt("no_kamar"));
                            String tanggalCheckin = response.getString("tanggal_checkin");
                            String tanggalCheckout = response.getString("tanggal_checkout");
                            String durasi = response.getString("durasi");
                            int totalHarga = response.getInt("total_harga");

                            TextView textViewNamaKamar = findViewById(R.id.namakamar);
                            textViewNamaKamar.setText(namaKamar);



                            TextView textViewNomorKamar = findViewById(R.id.nomor);
                            textViewNomorKamar.setText(nomorKamar);

                            TextView textViewCheckin = findViewById(R.id.checkin);
                            textViewCheckin.setText(tanggalCheckin);

                            TextView textViewCheckout = findViewById(R.id.checkout);
                            textViewCheckout.setText(tanggalCheckout);

                            TextView textViewDurasi = findViewById(R.id.durasi);
                            textViewDurasi.setText(durasi);

                            TextView textViewTotalHarga = findViewById(R.id.total);
                            textViewTotalHarga.setText("Rp " + totalHarga);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Tangani error saat mengambil data
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences2.getString("token", null);
                headers.put("Authorization", token);
                return headers;
            }
        };


        // Tambahkan request ke antrian request Volley
        Volley.newRequestQueue(this).add(request);
    }

}