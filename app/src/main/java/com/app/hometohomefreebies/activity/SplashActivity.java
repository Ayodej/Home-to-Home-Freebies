package com.app.hometohomefreebies.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.ActivitySplashBinding;
import com.app.hometohomefreebies.model.User;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private final Context context = SplashActivity.this;

    private ActivitySplashBinding binding;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getMyData();
    }

    @SuppressLint({"CheckResult", "HardwareIds"})
    private void getMyData(){
        SharedPreferences pref = getSharedPreferences(getString(R.string.pref_user_data), Context.MODE_PRIVATE);
        String token = pref.getString("token", "");
        if(TextUtils.isEmpty(token)){
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }else{
            Config.token = "Bearer " + token;
            apiService
                    .myData("notificationToken", Settings.Secure.getString(
                            getContentResolver(),
                            Settings.Secure.ANDROID_ID
                    ))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<User>() {
                        @Override
                        public void onSuccess(User user) {
                            Config.user = user;
                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            startActivity(new Intent(context, LoginActivity.class));
                        }
                    });
        }
    }
}