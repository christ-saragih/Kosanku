package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextView tv_login;
    private boolean passwordShowing = false;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private EditText nohpEditText;
    private EditText alamatEditText;
    private Button registerButton;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // agar actionbarnya menghilang
        if(getSupportActionBar () != null) {
            getSupportActionBar().hide();
        }


        // buat pindah ke halaman login
        tv_login = findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });



        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        ImageView passwordIcon = findViewById(R.id.passwordIcon);
        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordShowing){
                    passwordShowing = false;
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.ic_eye);
                }else {
                    passwordShowing = true;
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.mata_hide);

                }

                passwordEditText.setSelection(passwordEditText.length());
            }
        });
        usernameEditText = findViewById(R.id.usernameEditText);
        alamatEditText = findViewById(R.id.alamatEditText);
        nohpEditText = findViewById(R.id.nohpEditText);
        registerButton = findViewById(R.id.registerButton);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String name = nameEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String nohp = nohpEditText.getText().toString();
                String alamat = alamatEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Memeriksa apakah inputan yang diperlukan sudah diisi atau tidak

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(username) || TextUtils.isEmpty(alamat) || TextUtils.isEmpty(nohp)) {
                    Toast.makeText(RegisterActivity.this, "Harap isi semua input terlebih dahulu", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }else if (!isNumeric(nohp)) {
                    Toast.makeText(RegisterActivity.this, "No Hp harus berupa angka", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
                else {
                    register(name, username, email, nohp, alamat, password);
                }
            }
        });

    }

    // Fungsi untuk menampilkan ProgressDialog
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(RegisterActivity.this);
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
    private void executeRequest(String name, String username, String email, String nohp, String alamat,  String password ) {
        showProgressDialog(); // Menampilkan ProgressDialog sebelum request Volley dimulai

        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/register";
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
                                String token = jsonResponse.getString("token");
                                int expiresIn = jsonResponse.getInt("expiresIn");
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                // Pindah ke LoginActivity
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // Tutup RegisterActivity agar tidak bisa kembali dengan tombol back
                            }else {
                                hideProgressDialog();
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        } else {
                            Log.e("error", "onErrorResponse: " + error.getMessage(), error);
                            Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("nama", name);
                    jsonObject.put("username", username);
                    jsonObject.put("email", email);
                    jsonObject.put("nohp", nohp);
                    jsonObject.put("alamat", alamat);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(stringRequest);
    }
    private void register(String name, String username, String email, String nohp, String alamat,  String password ) {

        executeRequest(name, username, email, nohp, alamat, password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private boolean isNumeric(String strNum) {
        try {
            Long.parseLong(strNum);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }



}