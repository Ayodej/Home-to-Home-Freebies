package com.app.hometohomefreebies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.adapter.ProductsAdapter;
import com.app.hometohomefreebies.databinding.ActivityProductsBinding;
import com.app.hometohomefreebies.model.Category;
import com.app.hometohomefreebies.model.Product;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ProductsActivity extends AppCompatActivity implements ProductsAdapter.Interaction {

    private Activity context = ProductsActivity.this;

    private ActivityProductsBinding binding;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    private Category category;

    private final List<Product> productList = new ArrayList<>();

    private ProductsAdapter productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        initProductsRecyclerView();
        initListeners();
        getProducts();
    }

    private void getIntentData(){
        category = (Category) getIntent().getSerializableExtra("category");
        binding.tvView.setText(category.getTitle());
    }

    private void initProductsRecyclerView(){
        productsAdapter = new ProductsAdapter(context, productList, this);
        RecyclerView.LayoutManager linearLayoutManager  = new LinearLayoutManager(context);
        binding.rvProducts.setLayoutManager(linearLayoutManager);
        binding.rvProducts.setAdapter(productsAdapter);
    }

    private void initListeners(){
        binding.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getProducts(){
        apiService
                .getProductsByCategoryId(category.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Product>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Product> c) {
                        productList.clear();
                        productList.addAll(c);
                        productsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    public void onProductClicked(Product product) {
        Intent intent = new Intent(context, ViewProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }
}