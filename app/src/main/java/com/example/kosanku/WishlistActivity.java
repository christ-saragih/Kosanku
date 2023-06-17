package com.example.kosanku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.impl.ClaimsHolder;

import org.json.JSONException;
import org.json.JSONObject;

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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WishlistActivity extends AppCompatActivity {
    private List<ModelKamar> listKamarw;


    private ImageView hapusBtn;
    private RecyclerView recyclerView;

    private AdapterKamar adapter;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });



        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        listKamarw = new ArrayList<ModelKamar>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new WishlistActivity.AdapterKamar(listKamarw);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        Log.d("c", String.valueOf("as"));
        getData();
    }
    private void getData() {
        Log.d("a", String.valueOf("as"));
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/wishlist";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if(success){
                                JSONArray jsonArray = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    ModelKamar modelKamar = new ModelKamar();
                                    modelKamar.setId(obj.getString("_id"));
                                    modelKamar.setNomor(obj.getInt("no_kamar"));
                                    modelKamar.setRating(obj.getInt("rating"));
                                    modelKamar.setNama(obj.getString("nama_kamar"));
                                    modelKamar.setKategori(obj.getString("kategori"));
                                    modelKamar.setJenis(obj.getString("jenis"));
                                    modelKamar.setHarga(obj.getInt("harga_kamar"));
                                    JSONArray gambarArray = obj.getJSONArray("gambar");
                                    modelKamar.setGambar(gambarArray.getString(0));
                                    listKamarw.add(modelKamar);
                                }
                            } else {
                                TextView tidakada = findViewById(R.id.tidakada);
                                tidakada.setVisibility(View.VISIBLE);

                                Toast.makeText(WishlistActivity.this, message, Toast.LENGTH_SHORT).show();
                            }



                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", null);
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    public class AdapterKamar extends RecyclerView.Adapter<AdapterKamar.ViewHolder> {

        private List<ModelKamar> listKamarw;

        public AdapterKamar(List<ModelKamar> listKamar) {
            this.listKamarw = listKamar;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kamar_wishlist, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.itemView.setOnClickListener(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ModelKamar modelKamar = listKamarw.get(position);
            holder.nama.setText(modelKamar.getNama());
            holder.kategori.setText(modelKamar.getKategori());
            holder.jenis.setText(modelKamar.getJenis());
            holder.nomor.setText("No " + String.valueOf(modelKamar.getNomor()));
            holder.rating.setText(String.valueOf(modelKamar.getRating()));
            holder.harga.setText("Rp " + String.valueOf(modelKamar.getHarga()));
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
        @Override
        public int getItemCount() {
            return (listKamarw != null) ? listKamarw.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView nama, kategori, jenis, harga, rating, deskripsi, nomor;
            private RoundedImageView gambar;


            public ViewHolder(View itemView) {
                super(itemView);
                nama = itemView.findViewById(R.id.tv_nama);
                nomor = itemView.findViewById(R.id.tv_nomor);
                kategori = itemView.findViewById(R.id.tv_kategori);
                jenis = itemView.findViewById(R.id.tv_jenis);
                harga = itemView.findViewById(R.id.tv_harga);
                rating = itemView.findViewById(R.id.tv_rating);
                gambar = itemView.findViewById(R.id.iv_gambar);
                hapusBtn = itemView.findViewById(R.id.button_hapus);
                hapusBtn.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                ModelKamar modelKamar = listKamarw.get(position);
                String nomorKamar = String.valueOf(modelKamar.getNomor());
                if (v.getId() == hapusBtn.getId()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Hapus Wishlist")
                            .setMessage("Anda yakin ingin menghapus kamar ini dari wishlist?")
                            .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteData(nomorKamar);

                                }
                            })
                            .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User membatalkan dialog, tidak ada tindakan yang diperlukan
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Intent intent = new Intent(v.getContext(), DetailKamarActivity.class);
                    intent.putExtra("nomor_kamar", nomorKamar);
                    v.getContext().startActivity(intent);}
            }
        }


    }

    private void deleteData(String nomorKamar) {
        String endpoint = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/wishlist";
        Uri.Builder builder = Uri.parse(endpoint).buildUpon();
        builder.appendQueryParameter("nomor", nomorKamar);
        String url = builder.build().toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                JSONObject deleted = response.getJSONObject("deleted");
                                int deletedCount = deleted.getInt("deletedCount");
                                if (deletedCount > 0) {
                                    Toast.makeText(WishlistActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(WishlistActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(WishlistActivity.this, "Data gagal dihapus", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(WishlistActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", null);
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }


}