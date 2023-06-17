package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ImageView button_back;
    private SharedPreferences sharedPreferences;

    private EditText nameEditText;
    private EditText emailEditText;
    private ProgressDialog progressDialog;
    private EditText usernameEditText;
    private EditText nohpEditText;
    private EditText alamatEditText;

    private Button editButton;

    SharedPreferences.Editor setData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        alamatEditText = findViewById(R.id.alamatEditText);
        nohpEditText = findViewById(R.id.nohpEditText);
        editButton = findViewById(R.id.updateButton);
            String nama = sharedPreferences.getString("nama", null);
            String email = sharedPreferences.getString("email", null);
            String alamat = sharedPreferences.getString("alamat", null);
            String username = sharedPreferences.getString("username", null);
            String nohp = sharedPreferences.getString("nohp", null);




            nameEditText.setText(String.valueOf(nama));
            emailEditText.setText(String.valueOf(email));
            alamatEditText.setText(String.valueOf(alamat));
            usernameEditText.setText(String.valueOf(username));
            nohpEditText.setText(String.valueOf(nohp));



        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String name = nameEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String nohp = nohpEditText.getText().toString();
                String alamat = alamatEditText.getText().toString();
                String email = emailEditText.getText().toString();


                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)  ||
                        TextUtils.isEmpty(username) || TextUtils.isEmpty(alamat) || TextUtils.isEmpty(nohp)) {
                    Toast.makeText(getApplicationContext(), "Harap isi semua input terlebih dahulu", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
                else {
                    update(name, username, email, nohp, alamat);
                }
            }
        });

    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    private void executeRequest(String name, String username, String email, String nohp, String alamat ) {
        showProgressDialog();

        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users";
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("status");
                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");

                            if (success) {

                                setData = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();;
                                setData.putString("nama", name);
                                setData.putString("username", username);
                                setData.putString("nohp", nohp);
                                setData.putString("email", email);
                                setData.putString("alamat", alamat);
                                setData.apply();


                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                hideProgressDialog();
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
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String errorMessage = new String(networkResponse.data);
                            Log.e("error", errorMessage);
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        } else {
                            Log.e("error", "onErrorResponse: " + error.getMessage(), error);
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void update(String name, String username, String email, String nohp, String alamat) {

        executeRequest(name, username, email, nohp, alamat);
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

