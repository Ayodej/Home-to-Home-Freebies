package com.app.hometohomefreebies.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.activity.EditProfileActivity;
import com.app.hometohomefreebies.activity.SplashActivity;
import com.app.hometohomefreebies.activity.ViewProductActivity;
import com.app.hometohomefreebies.adapter.ProfileProductsAdapter;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.FragmentProfileBinding;
import com.app.hometohomefreebies.model.Product;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
        binding.btnLogout.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme);

            builder.setMessage("Are you sure you want to logout?");
            builder.setCancelable(true);

            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                Intent intent = new Intent(requireContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                SharedPreferences pref = requireContext().getSharedPreferences(getString(R.string.pref_user_data), Context.MODE_PRIVATE);
                pref.edit().clear().apply();
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
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