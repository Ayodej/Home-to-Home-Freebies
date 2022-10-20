package com.app.hometohomefreebies.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.ActivityLoginBinding;
import com.app.hometohomefreebies.model.Authorize;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private final Context context = LoginActivity.this;

    private ActivityLoginBinding binding;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initListeners();
    }

    private void initListeners(){
        binding.tvSignUp.setOnClickListener(view -> {
            startActivity(new Intent(context, RegisterActivity.class));
        });

        binding.btnLogin.setOnClickListener(view -> {
            if(binding.etEmail.getText().toString().isEmpty() || binding.etPassword.getText().toString().isEmpty()){
                Toast.makeText(context, "Please fill all the required fields", Toast.LENGTH_SHORT).show();
            }else{
                login();
            }
        });
    }

    @SuppressLint({"CheckResult", "HardwareIds"})
    private void login(){

        binding.btnLogin.setText("");
        binding.loginProgress.setVisibility(View.VISIBLE);

        apiService
                .loginUser(
                        binding.etEmail.getText().toString(),
                        binding.etPassword.getText().toString(),
                        "notificationToken",
                        Settings.Secure.getString(
                                getContentResolver(),
                                Settings.Secure.ANDROID_ID
                        )
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Authorize>() {
                    @Override
                    public void onSuccess(Authorize authorize) {
                        binding.btnLogin.setText("SIGN IN");
                        binding.loginProgress.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.pref_user_data), Context.MODE_PRIVATE).edit();
                        editor.putString("token", authorize.getAccessToken());
                        editor.apply();

                        Intent intent = new Intent(context, SplashActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.btnLogin.setText("SIGN IN");
                        binding.loginProgress.setVisibility(View.GONE);

                        Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}