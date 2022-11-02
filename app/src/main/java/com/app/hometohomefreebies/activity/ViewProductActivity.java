package com.app.hometohomefreebies.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.ActivityViewProductBinding;
import com.app.hometohomefreebies.model.Chat;
import com.app.hometohomefreebies.model.Product;
import com.app.hometohomefreebies.model.User;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class ViewProductActivity extends AppCompatActivity {

    private final Activity context = ViewProductActivity.this;

    private ActivityViewProductBinding binding;

    private Product product;

    private int callPhonePermissionRequest = 1;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        setData();
        initListeners();
    }

    private void getIntentData(){
        product = (Product) getIntent().getSerializableExtra("product");
    }

    private void setData(){
        Glide.with(context)
                .load(product.getImageList().get(0).getPath())
                .into(binding.ivProductImg);

        if(!product.getUser().getImage().isEmpty()){
            Glide.with(context)
                    .load(product.getUser().getImage())
                    .into(binding.profileImg);
        }

        binding.tvTitle.setText(product.getTitle());
        binding.tvDescription.setText(product.getDescription());
        binding.tvPhone.setText(product.getUser().getPhone());
        binding.tvAddress.setText(product.getAddress());

        if(Objects.equals(product.getUser().getId(), Config.user.getId())){
            binding.btnDelete.setVisibility(View.VISIBLE);
        }else{
            binding.btnDelete.setVisibility(View.GONE);
        }
    }

    private void initListeners(){
        binding.ibBack.setOnClickListener(view -> finish());

        binding.btnCall.setOnClickListener(view -> call());

        binding.btnDelete.setOnClickListener(view -> {
            deleteProduct();
        });

        binding.btnSendMessage.setOnClickListener(view -> {
            List<User> userList = new ArrayList<>();
            userList.add(product.getUser());
            Intent intent = new Intent(context, MessagesActivity.class);
            intent.putExtra("chat", new Chat(userList));
            startActivity(intent);
        });
    }

    private void call(){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + product.getUser().getPhone()));
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    context,
                    new String[]{Manifest.permission.CALL_PHONE},
                    callPhonePermissionRequest
            );
        }else{
            startActivity(callIntent);
        }
    }

    private void deleteProduct(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);

        builder.setMessage("Are you sure you want to delete this product?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            deleteProductCall();
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("CheckResult")
    private void deleteProductCall(){
        apiService
                .deleteProduct(
                        product.getId()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {
                        Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == callPhonePermissionRequest){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
            } else {
                call();
            }
        }

    }
}