package com.app.hometohomefreebies.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.databinding.ActivityViewProductBinding;
import com.app.hometohomefreebies.model.Product;
import com.bumptech.glide.Glide;

public class ViewProductActivity extends AppCompatActivity {

    private final Activity context = ViewProductActivity.this;

    private ActivityViewProductBinding binding;

    private Product product;

    private int callPhonePermissionRequest = 1;

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
    }

    private void initListeners(){
        binding.ibBack.setOnClickListener(view -> finish());

        binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
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