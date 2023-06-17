package com.example.kosanku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PemesananActivity extends AppCompatActivity {
    private List<ModelKamar> listKamarw;


    private ImageView hapusBtn;
    private Button detail;
    private RecyclerView recyclerView;

    private AdapterKamar adapter;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemesanan);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_pemesanan);
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

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        listKamarw = new ArrayList<ModelKamar>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new AdapterKamar(listKamarw);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getData();
    }

    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        overridePendingTransition(0, 0);
        finish();
    }

    private void getData() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/pemesanan";
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
                                    modelKamar.setNama(obj.getString("nama_kamar"));
                                    modelKamar.setCheckin(obj.getString("tanggal_checkin"));
                                    modelKamar.setCheckout(obj.getString("tanggal_checkout"));
                                    modelKamar.setDurasi(obj.getString("durasi"));
                                    modelKamar.setJenis(obj.getString("jenis"));
                                    modelKamar.setTotal_harga(obj.getInt("total_harga"));
                                    JSONArray gambarArray = obj.getJSONArray("gambar");
                                    modelKamar.setGambar(gambarArray.getString(0));
                                    listKamarw.add(modelKamar);

                                }


                            } else {
                                TextView tidakada = findViewById(R.id.tidakada);
                                tidakada.setVisibility(View.VISIBLE);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kamar_pesan, parent, false);
            AdapterKamar.ViewHolder viewHolder = new AdapterKamar.ViewHolder(view);
            viewHolder.itemView.setOnClickListener(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ModelKamar modelKamar = listKamarw.get(position);
            holder.nama.setText(modelKamar.getNama());
            holder.jenis.setText(modelKamar.getJenis());
            holder.checkin.setText(modelKamar.getCheckin());
            holder.checkout.setText(modelKamar.getCheckout());
            holder.durasi.setText(modelKamar.getDurasi());
            holder.nomor.setText(String.valueOf(modelKamar.getNomor()));
            holder.harga.setText("Rp " + String.valueOf(modelKamar.getTotal_harga()));

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
            private TextView nama, kategori, jenis, harga, rating, deskripsi, nomor, checkin, checkout, durasi;
            private RoundedImageView gambar;



            public ViewHolder(View itemView) {
                super(itemView);
                nama = itemView.findViewById(R.id.tv_nama);
                nomor = itemView.findViewById(R.id.tv_nomor);
                jenis = itemView.findViewById(R.id.tv_jenis);
                harga = itemView.findViewById(R.id.tv_harga);
                checkout = itemView.findViewById(R.id.tv_checkout);
                checkin = itemView.findViewById(R.id.tv_checkin);
                durasi = itemView.findViewById(R.id.tv_durasi);
                gambar = itemView.findViewById(R.id.iv_gambar);
                hapusBtn = itemView.findViewById(R.id.button_hapus);
                detail = itemView.findViewById(R.id.detail);
                hapusBtn.setOnClickListener(this);
                detail.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                ModelKamar modelKamar = listKamarw.get(position);
                String nomorKamar = String.valueOf(modelKamar.getNomor());
                if (v.getId() == hapusBtn.getId()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Pemesanan Selesai")
                            .setMessage("Anda yakin ingin menghapus pemesanan?")
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
                } else if (v.getId() == detail.getId()){
                    Intent intent = new Intent(v.getContext(), DetailPemesananActivity.class);
                    intent.putExtra("nomor_kamar", nomorKamar);
                    v.getContext().startActivity(intent);}
            }
        }


    }

    private void deleteData(String nomorKamar) {
        String endpoint = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/pemesanan";
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
                                    Toast.makeText(getApplicationContext(), "Pesanan berhasil dihapus", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Data gagal dihapus", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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