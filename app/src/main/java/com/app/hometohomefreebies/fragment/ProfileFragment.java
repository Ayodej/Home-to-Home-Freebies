package com.app.hometohomefreebies.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.activity.EditProfileActivity;
import com.app.hometohomefreebies.activity.ViewProductActivity;
import com.app.hometohomefreebies.adapter.ProfileProductsAdapter;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.FragmentProfileBinding;
import com.app.hometohomefreebies.model.Product;
import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment implements ProfileProductsAdapter.Interaction {

    private FragmentProfileBinding binding;

    private ProfileProductsAdapter productsAdapter;

    private final int PICK_PROFILE_IMG = 1;

    private Uri profileImgUri;

    private String profileImgPath;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProfileBinding.bind(view);

        setData();
        initProductsRecyclerView();
        initListeners();
    }

    private void setData(){
        Glide.with(requireContext())
                .load(Config.user.getImage())
                .into(binding.profileImg);

        binding.tvUsername.setText(Config.user.getFirstName() + " " + Config.user.getLastName());
        binding.tvPhone.setText(Config.user.getPhone());
    }

    private void initListeners(){
        binding.btnEditProfile.setOnClickListener(view -> startActivity(new Intent(requireContext(), EditProfileActivity.class)));
    }

    private void initProductsRecyclerView(){
        productsAdapter = new ProfileProductsAdapter(requireContext(), Config.user.getProducts(), this);
        RecyclerView.LayoutManager gridLayoutManager  = new GridLayoutManager(requireContext(), 3);
        binding.rvProducts.setLayoutManager(gridLayoutManager);
        binding.rvProducts.setAdapter(productsAdapter);
    }

    @Override
    public void onProductClicked(Product product) {
        Intent intent = new Intent(requireContext(), ViewProductActivity.class);
        product.setUser(Config.user);
        intent.putExtra("product", product);
        startActivity(intent);
    }
}