package com.app.hometohomefreebies.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.activity.LoginActivity;
import com.app.hometohomefreebies.activity.MainActivity;
import com.app.hometohomefreebies.activity.ProductsActivity;
import com.app.hometohomefreebies.activity.ViewProductActivity;
import com.app.hometohomefreebies.adapter.CategoriesAdapter;
import com.app.hometohomefreebies.adapter.ProductsAdapter;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.FragmentHomeBinding;
import com.app.hometohomefreebies.model.Category;
import com.app.hometohomefreebies.model.Product;
import com.app.hometohomefreebies.model.User;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements ProductsAdapter.Interaction, CategoriesAdapter.Interaction {

    private ApiService apiService;

    private FragmentHomeBinding binding;

    public static final List<Category> categoryList = new ArrayList<>();

    private final List<Product> productList = new ArrayList<>();

    private CategoriesAdapter categoriesAdapter;

    private ProductsAdapter productsAdapter;

    ActivityResultLauncher<Intent> viewProductActivityLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);

        apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        initListeners();
        initCategoriesRecyclerView();
        initProductsRecyclerView();
        getCategories();
        getProducts();
    }

    private void initListeners(){
        viewProductActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        getProducts();
                    }
                });

        binding.swipeRefreshLayout.setOnRefreshListener(this::getProducts);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 2){
                    search(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        binding.etSearch.addTextChangedListener(textWatcher);

    }

    private void initCategoriesRecyclerView(){
        categoriesAdapter = new CategoriesAdapter(requireContext(), categoryList, this);
        RecyclerView.LayoutManager linearLayoutManager  = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvCategories.setLayoutManager(linearLayoutManager);
        binding.rvCategories.setAdapter(categoriesAdapter);
    }

    private void initProductsRecyclerView(){
        productsAdapter = new ProductsAdapter(requireContext(), productList, this);
        RecyclerView.LayoutManager linearLayoutManager  = new LinearLayoutManager(requireContext());
        binding.rvProducts.setLayoutManager(linearLayoutManager);
        binding.rvProducts.setAdapter(productsAdapter);
    }

    @SuppressLint("CheckResult")
    private void search(String query){
        apiService
                .search(query)
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

    @SuppressLint("CheckResult")
    private void getCategories(){
        apiService
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Category>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Category> c) {
                        categoryList.clear();
                        categoryList.addAll(c);
                        categoriesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @SuppressLint("CheckResult")
    void getProducts(){
        apiService
                .getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Product>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Product> c) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                        productList.clear();
                        productList.addAll(c);
                        productsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onProductClicked(Product product) {
        Intent intent = new Intent(requireContext(), ViewProductActivity.class);
        intent.putExtra("product", product);
        viewProductActivityLauncher.launch(intent);
    }

    @Override
    public void onCategoryClicked(Category category) {
        Intent intent = new Intent(requireContext(), ProductsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}