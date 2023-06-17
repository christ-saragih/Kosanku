package com.example.kosanku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailKamarActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<String> imageUrls;
    private ImageSliderAdapter sliderAdapter;
    private LinearLayout indicatorContainer;
    private TextView namaKamar;
    private SharedPreferences sharedPreferences;
    private TextView kategoriKamar;
    private TextView jenisKamar;
    private ImageView tambah_wishlist, button_back;
    private TextView hargaKamar, nomorKamars;
    private RatingBar ratingKamar;
    private TextView deskripsiKamar;

    SharedPreferences.Editor setData;
    private String selectedDate2 = "";
    private String selectedDate = "";
    private  Button pesan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pesan= findViewById(R.id.pesan);
        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailKamarActivity.this);
                builder.setTitle("Pilih Tanggal");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.form, null);
                builder.setView(dialogView);

                ImageView buttonDatePicker = dialogView.findViewById(R.id.tanggalCheckin);
                TextView textViewSelectedDate = dialogView.findViewById(R.id.textViewSelectedDate);
                ImageView buttonDatePicker2 = dialogView.findViewById(R.id.tanggalCheckin2);
                TextView textViewSelectedDate2 = dialogView.findViewById(R.id.textViewSelectedDate2);


                buttonDatePicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailKamarActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        String monthName = new SimpleDateFormat("MMMM", new Locale("id")).format(new Date(year - 1900, month, dayOfMonth));
                                        selectedDate = dayOfMonth + " " + monthName + " " + year;
                                        textViewSelectedDate.setText(selectedDate);
                                        setData = getSharedPreferences("pesanan", MODE_PRIVATE).edit();
                                        setData.putString("checkin", selectedDate);
                                        setData.apply();
                                    }
                                }, year, month, dayOfMonth);

                        datePickerDialog.show();
                    }
                });

                buttonDatePicker2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int year2 = calendar.get(Calendar.YEAR);
                        int month2 = calendar.get(Calendar.MONTH);
                        int dayOfMonth2 = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailKamarActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year2, int month2, int dayOfMonth2) {
                                        String monthName2 = new SimpleDateFormat("MMMM", new Locale("id")).format(new Date(year2 - 1900, month2, dayOfMonth2));
                                        selectedDate2 = dayOfMonth2 + " " + monthName2 + " " + year2;
                                        textViewSelectedDate2.setText(selectedDate2);
                                        setData = getSharedPreferences("pesanan", MODE_PRIVATE).edit();
                                        setData.putString("checkout", selectedDate2);
                                        setData.apply();

                                    }
                                }, year2, month2, dayOfMonth2);

                        datePickerDialog.show();
                    }
                });

                builder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (selectedDate.isEmpty() || selectedDate2
                                .isEmpty()) {
                            Toast.makeText(DetailKamarActivity.this, "Mohon pilih tanggal check-in dan check-out", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("pesanan", MODE_PRIVATE);
                        String checkin = sharedPreferences.getString("checkin", "");
                        String checkout = sharedPreferences.getString("checkout", "");

                        if (checkin.isEmpty() || checkout.isEmpty()) {
                            Toast.makeText(DetailKamarActivity.this, "Mohon pilih tanggal check-in dan check-out", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(getApplicationContext(), DetailPesanActivity.class);
                        startActivity(intent);
                         }
                });

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        button_back = findViewById(R.id.back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        namaKamar = findViewById(R.id.tv_nama);
        kategoriKamar = findViewById(R.id.tv_kategori);
        jenisKamar = findViewById(R.id.tv_jenis);
        hargaKamar = findViewById(R.id.tv_harga);
        nomorKamars = findViewById(R.id.tv_nomor);
        ratingKamar = findViewById(R.id.tv_rating);
        deskripsiKamar = findViewById(R.id.tv_deskripsi);
        tambah_wishlist = findViewById(R.id.tambah_wishlist);

        viewPager = findViewById(R.id.viewPager);
        imageUrls = new ArrayList<>();

        String endpoint = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/detail";
        Uri.Builder builder = Uri.parse(endpoint).buildUpon();
        Intent intent = getIntent();
        String nomorKamar = intent.getStringExtra("nomor_kamar");
        builder.appendQueryParameter("nomor", nomorKamar);
        String url = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String nama = response.getString("nama");
                            String kategori = response.getString("kategori");
                            String jenis = response.getString("Jenis");
                            int harga = response.getInt("harga");
                            float rating = (float) response.getDouble("rating");
                            String deskripsi = response.getString("deskripsi");
                            JSONArray gambarArray = response.getJSONArray("gambar");
                            for (int i = 0; i < gambarArray.length(); i++) {
                                String imageUrl = gambarArray.getString(i);
                                imageUrls.add(imageUrl);
                            }

                            namaKamar.setText(nama);
                            nomorKamars.setText(nomorKamar);
                            kategoriKamar.setText(kategori);
                            jenisKamar.setText(jenis);
                            hargaKamar.setText("Rp " + String.valueOf(harga));
                            ratingKamar.setRating(rating);
                            deskripsiKamar.setText(deskripsi);


                            setData = getSharedPreferences("pesanan", MODE_PRIVATE).edit();
                            setData.putString("nama", nama);
                            setData.putString("nomor", nomorKamar);
                            setData.putString("jenis", jenis);
                            setData.putString("kategori", kategori);
                            setData.putString("harga", String.valueOf(harga));
                            setData.putString("gambar", gambarArray.getString(0));
                            setData.apply();

                            setupImageSlider();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailKamarActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });


        queue.add(jsonObjectRequest);


        tambah_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noKamar = intent.getStringExtra("nomor_kamar");
                wishlist(noKamar);
            }
        });
    }



    private void setupImageSlider() {
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, imageUrls);
        viewPager.setAdapter(sliderAdapter);
    }

    private void executeRequest(String nomorKamar) {

        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/wishlist";
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
                                Toast.makeText(DetailKamarActivity.this, message, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(DetailKamarActivity.this, message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DetailKamarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                        } else {
                            Log.e("error", "onErrorResponse: " + error.getMessage(), error);
                            Toast.makeText(DetailKamarActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("no_kamar", nomorKamar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", null);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(DetailKamarActivity.this);
        requestQueue.add(stringRequest);
    }

    private void wishlist(String nomorKamar) {

        executeRequest(nomorKamar);
    }
}
