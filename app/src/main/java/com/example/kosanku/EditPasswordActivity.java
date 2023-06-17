package com.example.kosanku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class EditPasswordActivity extends AppCompatActivity {

    private boolean passwordShowing = false;
    private boolean passwordShowing2 = false;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        if(getSupportActionBar () != null) {
            getSupportActionBar().hide();
        }
        EditText passwordEditText = findViewById(R.id.passwordEditText);
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

        EditText passwordEditText2 = findViewById(R.id.passwordEditText2);
        ImageView passwordIcon2 = findViewById(R.id.passwordIcon2);
        passwordIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordShowing2){
                    passwordShowing2 = false;
                    passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon2.setImageResource(R.drawable.ic_eye);
                }else {
                    passwordShowing2 = true;
                    passwordEditText2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon2.setImageResource(R.drawable.mata_hide);

                }

                passwordEditText2.setSelection(passwordEditText2.length());
            }
        });

        ImageView button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });
        Button editButton = findViewById(R.id.updateButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String oldpassword = passwordEditText.getText().toString();
                String newpassword = passwordEditText2.getText().toString();


                if (TextUtils.isEmpty(oldpassword) || TextUtils.isEmpty(newpassword)) {
                    Toast.makeText(getApplicationContext(), "Harap isi semua input terlebih dahulu", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
                else {
                    update(oldpassword, newpassword);
                }
            }
        });




    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(EditPasswordActivity.this);
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
    private void executeRequest(String oldpassword, String newpassword) {
        showProgressDialog();

        String url = "https://ap-southeast-1.aws.data.mongodb-api.com/app/kosanku-auwfb/endpoint/auth/users/password";
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
                    jsonObject.put("oldpassword", oldpassword);
                    jsonObject.put("newpassword", newpassword);
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

    private void update(String oldpassword, String newpassword) {

        executeRequest(oldpassword, newpassword);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


}

