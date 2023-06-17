package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CariKamarActivity extends AppCompatActivity {

    private List<ModelKamar> listKamar;
    private RecyclerView recyclerView;
    private AdapterKamar adapter;

    Button button_lihat;

    ImageView button_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_kamar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        listKamar = new ArrayList<ModelKamar>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new AdapterKamar(listKamar);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getData();
    }

    private void getData() {
        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        String endpoint = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/cari";
        Uri.Builder builder = Uri.parse(endpoint).buildUpon();
        builder.appendQueryParameter("nama", keyword);
        String url = builder.build().toString();
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");
                            if (success) {
                                JSONArray data = response.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    ModelKamar modelKamar = new ModelKamar();
                                    modelKamar.setId(obj.getString("_id"));
                                    modelKamar.setNomor(obj.getInt("nomor"));
                                    modelKamar.setNama(obj.getString("nama"));
                                    modelKamar.setKategori(obj.getString("kategori"));
                                    modelKamar.setJenis(obj.getString("Jenis"));
                                    modelKamar.setHarga(obj.getInt("harga"));
                                    modelKamar.setRating(obj.getInt("rating"));
                                    modelKamar.setDeskripsi(obj.getString("deskripsi"));
                                    modelKamar.setStatus(obj.getString("status"));
                                    JSONArray gambarArray = obj.getJSONArray("gambar");
                                    modelKamar.setGambar(gambarArray.getString(0));
                                    listKamar.add(modelKamar);

                                 }
                                int totalData = listKamar.size();
                                String totalDataText;
                                if (totalData == 0) {
                                    totalDataText = "0 Kamar untuk kamu";
                                } else {
                                    totalDataText = totalData + " Kamar untuk kamu";
                                }
                                TextView tvTotalData = findViewById(R.id.tv_total);
                                tvTotalData.setText(totalDataText);

                            }else {
                                TextView tvTotalData = findViewById(R.id.tv_total);
                                tvTotalData.setText("0 Kamar untuk kamu");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                TextView tidakada = findViewById(R.id.tidakada);
                                tidakada.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    public class AdapterKamar extends RecyclerView.Adapter<AdapterKamar.ViewHolder> {

        private List<ModelKamar> listKamar;

        public AdapterKamar(List<ModelKamar> listKamar) {
            this.listKamar = listKamar;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kamar_list, parent, false);
            AdapterKamar.ViewHolder viewHolder = new AdapterKamar.ViewHolder(view);
            viewHolder.itemView.setOnClickListener(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterKamar.ViewHolder holder, int position) {
            ModelKamar modelKamar = listKamar.get(position);
            holder.nama.setText(modelKamar.getNama());
            holder.jenis.setText(modelKamar.getJenis());
            holder.status.setText(modelKamar.getStatus());
            holder.harga.setText("Rp " + String.valueOf(modelKamar.getHarga()));
            holder.rating.setRating(modelKamar.getRating());
            holder.nomor.setText(String.valueOf(modelKamar.getNomor()));
            Picasso.get().load(modelKamar.getGambar()).fit().centerCrop().into(holder.gambar);

        }


        @Override
        public int getItemCount() {
            return (listKamar != null) ? listKamar.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
            private TextView nama, kategori, jenis, harga, nomor, status, deskripsi;
            private ImageView gambar;
            private RatingBar rating;

            public ViewHolder(View itemView) {
                super(itemView);
                nama = itemView.findViewById(R.id.tv_nama);
                kategori = itemView.findViewById(R.id.tv_kategori);
                jenis = itemView.findViewById(R.id.tv_jenis);
                harga = itemView.findViewById(R.id.tv_harga);
                rating = itemView.findViewById(R.id.tv_rating);
                nomor = itemView.findViewById(R.id.tv_nomor);
                status = itemView.findViewById(R.id.tv_status);
                gambar = itemView.findViewById(R.id.iv_gambar);
                button_lihat = itemView.findViewById(R.id.button_lihat);
                button_lihat.setOnClickListener(this);
                itemView.setFocusable(true);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                ModelKamar modelKamar = listKamar.get(position);
                String nomorKamar = String.valueOf(modelKamar.getNomor());
                if (view.getId() == button_lihat.getId()) {
                    Intent intent = new Intent(view.getContext(), DetailKamarActivity.class);
                    intent.putExtra("nomor_kamar", nomorKamar);
                    view.getContext().startActivity(intent);
                }
            }


        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}