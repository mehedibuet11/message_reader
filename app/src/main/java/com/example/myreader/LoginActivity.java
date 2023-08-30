package com.example.myreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail, device_key;
    private CheckBox rememberMeCheckbox;
    private ApiService apiService; // Corrected field declaration

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userEmail);
        device_key = findViewById(R.id.device_key);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inside the loginButton OnClickListener
                String username = userEmail.getText().toString();
                String password = device_key.getText().toString();
                String deviceIp = getAndroidId(LoginActivity.this); // Retrieve the device IP here, if needed

                makeApiCall(username, password, deviceIp);
            }
        });
    }
    private void makeApiCall(String user_email, String device_key, String device_ip) {
        // Create a Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://mahadi.servicesbd.top")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create the ApiService
        ApiService apiService = retrofit.create(ApiService.class);

        // Make the API call
        Call<YourResponseModel> call = apiService.connectWithMobileApp(user_email, device_key, device_ip);

        call.enqueue(new Callback<YourResponseModel>() {
            @Override
            public void onResponse(Call<YourResponseModel> call, Response<YourResponseModel> response) {
                if (response.isSuccessful()) {
                    YourResponseModel YourResponseModel = response.body();
                    if (YourResponseModel != null && YourResponseModel.getStatus().equals("1")) {
                        //save logged info
                        LoginManager.saveLoginInfo(LoginActivity.this,user_email,YourResponseModel.getMessage());

                        ToastHelper.showCustomToast(LoginActivity.this, "Your device is connected successfully!", null);
                        // Proceed to the main activity or perform other actions
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Finish the login activity
                            }
                        },2000);


                    } else {
                        String message = YourResponseModel != null ? YourResponseModel.getMessage() : "Validation failed";
                        ToastHelper.showCustomToast(LoginActivity.this, message, null);
                    }
                } else {
                    ToastHelper.showCustomToast(LoginActivity.this, "Failed to connect with the server", null);
                }
            }

            @Override
            public void onFailure(Call<YourResponseModel> call, Throwable t) {
                ToastHelper.showCustomToast(LoginActivity.this, "Network request failed", null);
            }
        });
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
