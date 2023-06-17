package com.example.kosanku;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    ColorStateList def;
    TextView item1;
    TextView item2;
    TextView item3;
    TextView select, tv_nama;

    ImageView search;
    EditText carikamar;

    private SharedPreferences sharedPreferences;
    private List<ModelKamar> listKamar;
    private List<ModelKamar> listKamar2;
    private RecyclerView recyclerView, recyclerView2;
    private AdapterKamar adapter, adapter2;

    boolean shouldGetData = false;
    String currentUrl = "";

    @Override
    protected void onResume() {
        super.onResume();
        carikamar.setText(""); // Mengatur EditText menjadi string kosong
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        ImageView wishlist = findViewById(R.id.wishlist);
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), WishlistActivity.class));
            }
        });

        search  = findViewById(R.id.search);
        carikamar = findViewById(R.id.carikamar);
        carikamar.setText("");

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = carikamar.getText().toString();
                Intent intent = new Intent(getApplicationContext(), CariKamarActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
        });


        tv_nama = findViewById(R.id.tv_nama);
        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);
        item1.setOnClickListener(this::onClick);
        item2.setOnClickListener(this::onClick);
        item3.setOnClickListener(this::onClick);
        select = findViewById(R.id.select);
        def = item2.getTextColors();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:

                        return true;
                    case R.id.bottom_kamar:
                        startActivity(new Intent(getApplicationContext(), KamarActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottom_pemesanan:
                        startActivity(new Intent(getApplicationContext(), PemesananActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottom_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);

                        finish();
                        return true;
                }
                return false;
            }
        });

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        if (sharedPreferences.contains("token")) {
            String token = sharedPreferences.getString("token", null);
            String nama = sharedPreferences.getString("nama", null);
            if (token != null) {
                String[] jwtParts = token.split("\\.");
                if (jwtParts.length == 3) {
                    String header = jwtParts[0];
                    String payload = jwtParts[1];
                    String signature = jwtParts[2];

                    String decodedHeader = new String(Base64.decode(header, Base64.URL_SAFE));
                    String decodedPayload = new String(Base64.decode(payload, Base64.URL_SAFE));

                    try {
                        JSONObject jsonPayload = new JSONObject(decodedPayload);

                        long expirationTime = jsonPayload.getLong("exp");
                        long currentTime = System.currentTimeMillis() / 1000;
                        Log.e("MainActivity", "currentTime: " + currentTime);
                        Log.e("MainActivity", "expiresIn: " + expirationTime);

                        if (currentTime < expirationTime) {
                            tv_nama.setText("Selamat Datang, " + String.valueOf(nama) + "!!");

                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // token tidak valid, kembalikan ke halaman login
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                // token tidak valid, kembalikan ke halaman login
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            // token tidak ada, kembalikan ke halaman login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        listKamar = new ArrayList<ModelKamar>();
        listKamar2 = new ArrayList<ModelKamar>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new AdapterKamar(listKamar, listKamar2, false);
        recyclerView.setAdapter(adapter);
        getData("https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/pria/tersedia/limit", false);



        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        adapter2 = new AdapterKamar(listKamar,listKamar2, true);
        recyclerView2.setAdapter(adapter2);
        getData("https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/recomendation", true);





    }

    public void onClick(View view) {
        if (view.getId() == R.id.item1){
            select.animate().x(0).setDuration(100);
            item1.setTextColor(Color.WHITE);
            item2.setTextColor(def);
            item3.setTextColor(def);
            shouldGetData = true;
            currentUrl = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/pria/tersedia/limit";
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);

        } else if (view.getId() == R.id.item2){
            item1.setTextColor(def);
            item2.setTextColor(Color.WHITE);
            item3.setTextColor(def);
            int size = item2.getWidth();
            select.animate().x(size).setDuration(100);
            shouldGetData = true;
            currentUrl = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/perempuan/tersedia/limit";
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
        }

        else if (view.getId() == R.id.item3){
            item1.setTextColor(def);
            item3.setTextColor(Color.WHITE);
            item2.setTextColor(def);
            int size = item2.getWidth() * 2;
            select.animate().x(size).setDuration(100);
            shouldGetData = true;
            currentUrl = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/kamar/campuran/tersedia/limit";
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
        }
        else {
            shouldGetData = false;
            currentUrl ="";
        }

        if (shouldGetData) {
            getData(currentUrl, false);
        } else {

        }




    }
    public void onBackPressed() {
        finishAffinity();
    }


    private void getData(String urls, boolean isRekomendasi) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url = urls;



        if (url.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "URL tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        List<ModelKamar> currentList;
        if (isRekomendasi) {
            currentList = listKamar2;
        } else {
            currentList = listKamar;
            currentList.clear(); // Membersihkan data sebelum menambahkan data baru
        }





        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
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
                                currentList.add(modelKamar);

                            }
                            Log.e("yes", "onResponse:" + listKamar );
                            progressBar.setVisibility(View.GONE);
                            if (isRekomendasi) {
                                adapter2.notifyDataSetChanged();
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }


    public class AdapterKamar extends RecyclerView.Adapter<AdapterKamar.ViewHolder> {

        private List<ModelKamar> listKamar;
        private List<ModelKamar> listKamar2;
        private boolean isRekomendasi;

        public AdapterKamar(List<ModelKamar> listKamar,  List<ModelKamar> listKamar2,  boolean isRekomendasi) {
            this.listKamar = listKamar;
            this.listKamar2 = listKamar2;
            this.isRekomendasi = isRekomendasi;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            ViewHolder viewHolder;
            if (isRekomendasi) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kamar_rekomendasi, parent, false);
                viewHolder = new ViewHolder(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kamar_kategori, parent, false);
                viewHolder = new ViewHolder(view);
            }
            view.setOnClickListener(viewHolder);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            if (isRekomendasi) {
                ModelKamar modelKamar2 = listKamar2.get(position);
                holder.jenis.setText(String.valueOf(modelKamar2.getJenis()));
                holder.kategori.setText(String.valueOf(modelKamar2.getKategori()));
                holder.nama.setText(String.valueOf(modelKamar2.getNama()));
                holder.rating.setText(String.valueOf(modelKamar2.getRating()));
                Picasso.get().load(modelKamar2.getGambar()).into(holder.gambar);
                Picasso.get()
                        .load(modelKamar2.getGambar())
                        .transform(new Transformation() {
                            @Override
                            public Bitmap transform(Bitmap source) {
                                Bitmap transformedBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
                                Canvas canvas = new Canvas(transformedBitmap);
                                RoundedDrawable roundedDrawable = new RoundedDrawable(source);
                                roundedDrawable.setCornerRadius(getResources().getDimension(R.dimen.rounded_corner_radius)); // Mengatur radius sudut melingkar

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
                        .into(holder.gambar);
            } else {
                ModelKamar modelKamar = listKamar.get(position);
                holder.harga.setText("Rp " + String.valueOf(modelKamar.getHarga()));
                holder.nama.setText(String.valueOf(modelKamar.getNama()));
                holder.rating.setText(String.valueOf(modelKamar.getRating()));
                Picasso.get().load(modelKamar.getGambar()).into(holder.gambar);
                Picasso.get()
                        .load(modelKamar.getGambar())
                        .transform(new Transformation() {
                            @Override
                            public Bitmap transform(Bitmap source) {
                                Bitmap transformedBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
                                Canvas canvas = new Canvas(transformedBitmap);
                                RoundedDrawable roundedDrawable = new RoundedDrawable(source);
                                roundedDrawable.setCornerRadius(getResources().getDimension(R.dimen.rounded_corner_radius)); // Mengatur radius sudut melingkar

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
                        .into(holder.gambar);

            }


        }


        @Override
        public int getItemCount() {
            if (isRekomendasi) {
                return (listKamar2 != null) ? listKamar2.size() : 0;
            } else {
                return (listKamar != null) ? listKamar.size() : 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {
            private TextView nama, harga, rating, jenis, kategori;
            private RoundedImageView gambar;

            public ViewHolder(View itemView) {
                super(itemView);
                jenis = itemView.findViewById(R.id.tv_jenis);
                kategori = itemView.findViewById(R.id.tv_kategori);
                nama = itemView.findViewById(R.id.tv_nama);
                harga = itemView.findViewById(R.id.tv_harga);
                rating = itemView.findViewById(R.id.tv_rating);
                gambar = itemView.findViewById(R.id.iv_gambar);
                itemView.setOnClickListener(this);

            }


            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (isRekomendasi) {
                        if (position >= 0 && position < listKamar2.size()) {
                            ModelKamar modelKamar = listKamar2.get(position);
                            String nomorKamar = String.valueOf(modelKamar.getNomor());
                            Intent intent = new Intent(view.getContext(), DetailKamarActivity.class);
                            intent.putExtra("nomor_kamar", nomorKamar);
                            view.getContext().startActivity(intent);
                        }
                    }
                    else {
                        if (position < listKamar.size()) {
                            ModelKamar modelKamar = listKamar.get(position);
                            String nomorKamar = String.valueOf(modelKamar.getNomor());
                            Intent intent = new Intent(view.getContext(), DetailKamarActivity.class);
                            intent.putExtra("nomor_kamar", nomorKamar);
                            view.getContext().startActivity(intent);
                        }
                    }
                }
            }
        }


        }


    }
