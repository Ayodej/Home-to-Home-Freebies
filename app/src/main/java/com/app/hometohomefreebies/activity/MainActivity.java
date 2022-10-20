package com.app.hometohomefreebies.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.databinding.ActivityMainBinding;
import com.app.hometohomefreebies.fragment.HomeFragment;
import com.app.hometohomefreebies.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private final Context context = MainActivity.this;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainFragment, homeFragment).commit();

        initListeners();
        initNavigation();
    }

    private void initListeners(){
        binding.fabAdd.setOnClickListener(view -> {
            startActivity(new Intent(context, AddProductActivity.class));
        });

        binding.flHome.setOnClickListener(view -> {
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, homeFragment).commit();
            binding.ivHome.setBackground(ContextCompat.getDrawable(context, R.drawable.circle3));
            binding.ivProfile.setBackground(null);
        });

        binding.flProfile.setOnClickListener(view -> {
            ProfileFragment profileFragment = new ProfileFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFragment, profileFragment).commit();
            binding.ivProfile.setBackground(ContextCompat.getDrawable(context, R.drawable.circle3));
            binding.ivHome.setBackground(null);
        });
    }

    private void initNavigation(){

    }

}