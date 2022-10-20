package com.app.hometohomefreebies.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.ActivityRegisterBinding;
import com.app.hometohomefreebies.model.Authorize;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;
import com.bumptech.glide.Glide;
import com.zhihu.matisse.Matisse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {

    private Activity context = RegisterActivity.this;

    private ActivityRegisterBinding binding;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    private final int PICK_PROFILE_IMG = 1;

    private Uri profileImgUri;

    private String profileImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initListeners();
    }

    private void initListeners(){
        binding.tvSignIn.setOnClickListener(view -> finish());

        binding.profileImg.setOnClickListener(view -> Config.pickImgFromGallery(context, PICK_PROFILE_IMG));

        binding.btnRegister.setOnClickListener(view -> {
            Config.hideKeyboard(context);
            if(validateForm()){
                if(profileImgPath == null){
                    Toast.makeText(context, "Profile image is required", Toast.LENGTH_SHORT).show();
                }else{
                    register();
                }
            }
        });

    }

    private boolean validateForm()  {

        boolean valid = true;

        String mFirstName = binding.etFirstName.getText().toString();
        if (TextUtils.isEmpty(mFirstName)) {
            binding.etFirstName.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etFirstName.setError(null);
        }

        String mLastName = binding.etLastName.getText().toString();
        if (TextUtils.isEmpty(mLastName)) {
            binding.etLastName.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etLastName.setError(null);
        }

        String mEmail = binding.etEmail.getText().toString();
        if (TextUtils.isEmpty(mEmail)) {
            binding.etEmail.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etEmail.setError(null);
        }

        String mPassword = binding.etPassword.getText().toString();
        if (TextUtils.isEmpty(mPassword)) {
            binding.etPassword.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etPassword.setError(null);
        }

        String mConfirmPassword = binding.etConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(mConfirmPassword)) {
            binding.etConfirmPassword.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etConfirmPassword.setError(null);
        }

        String mPhone = binding.etPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(mPhone)) {
            binding.etPhoneNumber.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etPhoneNumber.setError(null);
        }

        if(!binding.etPassword.getText().toString().equals(binding.etConfirmPassword.getText().toString())){
            binding.etConfirmPassword.setError(getString(R.string.err_required));
            valid = false;
            Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show();
        }else{
            binding.etConfirmPassword.setError(null);
        }

        return valid;
    }

    @SuppressLint({"CheckResult", "HardwareIds"})
    private void register(){

        binding.btnRegister.setText("");
        binding.registerProgress.setVisibility(View.VISIBLE);

        apiService
                .registerUser(
                        binding.etFirstName.getText().toString(),
                      binding.etLastName.getText().toString(),
                      binding.etEmail.getText().toString(),
                      binding.etPassword.getText().toString(),
                      binding.etPhoneNumber.getText().toString(),
                        Config.prepareFilePart("image", profileImgPath),
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
                        binding.btnRegister.setText("Register");
                        binding.registerProgress.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.pref_user_data), Context.MODE_PRIVATE).edit();
                        editor.putString("token", authorize.getAccessToken());
                        editor.apply();

                        Intent intent = new Intent(context, SplashActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {

                        binding.btnRegister.setText("Register");
                        binding.registerProgress.setVisibility(View.GONE);

                        Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //==============Override Methods==============//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PICK_PROFILE_IMG){
                assert data != null;
                profileImgUri = Matisse.obtainResult(data).get(0);
                profileImgPath = Matisse.obtainPathResult(data).get(0);
                Glide.with(context)
                        .load(profileImgPath)
                        .into(binding.profileImg);
            }
        }

    }
}