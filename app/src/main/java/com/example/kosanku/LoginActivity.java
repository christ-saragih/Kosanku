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



import android.content.Context;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private TextView tv_signup;

    private EditText emailusernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private boolean passwordShowing = false;
    SharedPreferences.Editor setData;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }


        tv_signup = findViewById(R.id.tv_signup);
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });


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


        emailusernameEditText = findViewById(R.id.emailusernameEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailusername = emailusernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // Memeriksa apakah inputan yang diperlukan sudah diisi atau tidak
                if (TextUtils.isEmpty(emailusername) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Harap isi semua input terlebih dahulu", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
                else {
                    login(emailusername, password);
                }
            }
        });
    }
    // Fungsi untuk menampilkan ProgressDialog
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
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
    private void executeRequest(String username,  String password ) {
        showProgressDialog(); // Menampilkan ProgressDialog sebelum request Volley dimulai

        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/login";
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
                                JSONObject user = jsonResponse.getJSONObject("user");
                                String nama = user.getString("nama");
                                String username = user.getString("username");
                                String nohp = user.getString("nohp");
                                String email = user.getString("email");
                                String alamat = user.getString("alamat");

                                String avatar = user.getString("avatar");

                                setData = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
                                setData.putString("token", token);
                                setData.putString("nama", nama);
                                setData.putString("username", username);
                                setData.putString("nohp", nohp);
                                setData.putString("email", email);
                                setData.putString("alamat", alamat);
                                setData.putString("avatar", avatar);
                                setData.putInt("expiresIn", expiresIn);

                                setData.apply();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                // Pindah ke LoginActivity
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Tutup RegisterActivity agar tidak bisa kembali dengan tombol back
                            }else {
                                hideProgressDialog();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        } else {
                            Log.e("error", "onErrorResponse: " + error.getMessage(), error);
                            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (isValidEmail(username)) {
                        jsonObject.put("email", username);
                        jsonObject.put("password", password);
                    }else {
                        jsonObject.put("username", username);
                        jsonObject.put("password", password);
                    }
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

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }
    private void login(String username,  String password ) {
        executeRequest(username, password);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


}





