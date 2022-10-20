package com.app.hometohomefreebies.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.ActivityEditProfileBinding;
import com.app.hometohomefreebies.model.Authorize;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;
import com.bumptech.glide.Glide;
import com.zhihu.matisse.Matisse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;

public class EditProfileActivity extends AppCompatActivity {

    private Activity context = EditProfileActivity.this;

    private ActivityEditProfileBinding binding;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    private final int PICK_PROFILE_IMG = 1;

    private Uri profileImgUri;

    private String profileImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setData();
        initListeners();
    }

    private void setData(){
        Glide.with(context)
                .load(Config.user.getImage())
                .into(binding.profileImg);

        binding.etFirstName.setText(Config.user.getFirstName());
        binding.etLastName.setText(Config.user.getLastName());
        binding.etPhoneNumber.setText(Config.user.getPhone());
    }

    private void initListeners(){
        binding.ibBack.setOnClickListener(view -> finish());

        binding.profileImg.setOnClickListener(view -> Config.pickImgFromGallery(context, PICK_PROFILE_IMG));

        binding.btnSubmit.setOnClickListener(view -> {
            Config.hideKeyboard(context);
            if(validateForm()){
                editProfile();
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


        String mPhone = binding.etPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(mPhone)) {
            binding.etPhoneNumber.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etPhoneNumber.setError(null);
        }

        return valid;
    }

    @SuppressLint("CheckResult")
    private void editProfile(){

        binding.btnSubmit.setText("");
        binding.submitProgress.setVisibility(View.VISIBLE);

        MultipartBody.Part filePart = null;

        if(profileImgPath != null){
            filePart = Config.prepareFilePart("image", profileImgPath);
        }

        apiService
                .editProfile(
                        binding.etFirstName.getText().toString(),
                        binding.etLastName.getText().toString(),
                        binding.etPhoneNumber.getText().toString(),
                        filePart
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Authorize>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(Authorize authorize) {
                        binding.btnSubmit.setText("Submit");
                        binding.submitProgress.setVisibility(View.GONE);

                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show();

                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                        binding.btnSubmit.setText("Submit");
                        binding.submitProgress.setVisibility(View.GONE);

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