package com.app.hometohomefreebies.activity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.adapter.AddImageAdapter;
import com.app.hometohomefreebies.adapter.ProductsAdapter;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.ActivityAddProductBinding;
import com.app.hometohomefreebies.fragment.HomeFragment;
import com.app.hometohomefreebies.model.Authorize;
import com.app.hometohomefreebies.model.Category;
import com.app.hometohomefreebies.model.Image;
import com.app.hometohomefreebies.model.Product;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.zhihu.matisse.Matisse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

public class AddProductActivity extends AppCompatActivity
        implements AddImageAdapter.Interaction,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener
{

    private final Activity context = AddProductActivity.this;

    private final List<String> imageList = new ArrayList<>();

    private ActivityAddProductBinding binding;

    private AddImageAdapter adapter;

    private final int PICK_PRODUCT_IMGS = 1;

    private final int permissionRequestFineLocation = 2;

    private int searchForLocationReqestCode = 2;

    private Double latitude = 0.0;
    private Double longitude = 0.0;

    private int selectedCategory = 0;

    GoogleMap mGoogleMap;

    private Marker marker = null;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageList.add("");
        initProductsRecyclerView();
        initListeners();

        if(googleServicesAvailable()){

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
        }
    }

    private void initListeners(){
        binding.ibSearchLocation.setOnClickListener(view -> {
            List<Place.Field> fields = new ArrayList<Place.Field>();
            fields.add(Place.Field.ID);
            fields.add(Place.Field.NAME);
            fields.add(Place.Field.LAT_LNG);

            Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, searchForLocationReqestCode);
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    if(imageList.size() < 2){
                        Toast.makeText(context, "Please select at least one image", Toast.LENGTH_SHORT).show();
                    }else{
                        addProduct();
                    }
                }
            }
        });

        binding.tvCategory.setOnClickListener(view -> selectCategory());
    }

    private Boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);

        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            assert dialog != null;
            dialog.show();
        }else{
            Toast.makeText(this, "Google Services isn't available for your device", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    private void initProductsRecyclerView(){
        adapter = new AddImageAdapter(context, imageList, this);
        RecyclerView.LayoutManager linearLayoutManager  = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.rvImages.setLayoutManager(linearLayoutManager);
        binding.rvImages.setAdapter(adapter);
    }

    private void selectCategory(){
        String[] categories = new String[HomeFragment.categoryList.size()];
        int i = 0;
        for (Category category:
                HomeFragment.categoryList) {
            categories[i] = category.getTitle();
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Category");
        builder.setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedCategory = HomeFragment.categoryList.get(which).getId();
                binding.tvCategory.setText(HomeFragment.categoryList.get(which).getTitle());
            }
        });
        builder.show();
    }

    private boolean validateForm()  {

        boolean valid = true;

        String mTitle = binding.etTitle.getText().toString();
        if (TextUtils.isEmpty(mTitle)) {
            binding.etTitle.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etTitle.setError(null);
        }

        String mDescription = binding.etDescription.getText().toString();
        if (TextUtils.isEmpty(mDescription)) {
            binding.etDescription.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etDescription.setError(null);
        }

        String mCategory = binding.tvCategory.getText().toString();
        if (TextUtils.isEmpty(mCategory)) {
            binding.tvCategory.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.tvCategory.setError(null);
        }

        String mLocation = binding.etLocation.getText().toString();
        if (TextUtils.isEmpty(mLocation)) {
            binding.etLocation.setError(getString(R.string.err_required));
            valid = false;
        } else {
            binding.etLocation.setError(null);
        }

        return valid;
    }

    @SuppressLint({"CheckResult", "HardwareIds"})
    private void addProduct(){

        binding.btnDone.setText("");
        binding.doneProgress.setVisibility(View.VISIBLE);

        List<MultipartBody.Part> multiPartImgList = new ArrayList<>();
        for (String img:
             imageList) {
            if(!img.isEmpty()){
                multiPartImgList.add(Config.prepareFilePart("images[]", img));
            }
        }

        apiService
                .addProduct(
                        binding.etTitle.getText().toString(),
                        binding.etDescription.getText().toString(),
                        binding.etLocation.getText().toString(),
                        latitude,
                        longitude,
                        selectedCategory,
                        multiPartImgList
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {
                        binding.btnDone.setText("DONE");
                        binding.doneProgress.setVisibility(View.GONE);

                        Toast.makeText(context, "Product added", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {

                        binding.btnDone.setText("DONE");
                        binding.doneProgress.setVisibility(View.GONE);

                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onAddImageClicked() {
        Config.pickImagesFromGallery(context, PICK_PRODUCT_IMGS);
    }

    @Override
    public void onDeleteImageClicked(int position) {
        imageList.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PICK_PRODUCT_IMGS){
                assert data != null;
                for (String image:
                        Matisse.obtainPathResult(data)) {
                    imageList.add(0, image);
                }
                adapter.notifyDataSetChanged();
            }

            if(requestCode == searchForLocationReqestCode){
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                setLocationData(Objects.requireNonNull(place.getLatLng()));
            }
        }
    }

    @SuppressLint("CheckResult")
    private void setLocationData(LatLng location){

        latitude = location.latitude;
        longitude = location.longitude;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if(Config.isNetworkConnected(context)){

            if(location == null){
                Toast.makeText(context, "Failed to get location info", Toast.LENGTH_LONG).show();
            }else{


                Runnable r = new Runnable() {
                    public void run() {
                        try {
                            List<Address> addressList = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                            );

                            if(addressList.size() > 0){
                                String address =
                                        addressList.get(0).getAddressLine(0);
                                binding.etLocation.setText(address);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                new Thread(r).start();
            }
        }else{
            Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
        }

        mGoogleMap.clear();
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                return true;
            }
        });

        marker = mGoogleMap.addMarker(new MarkerOptions().position(location).title("Location").snippet(""));
        marker.showInfoWindow();;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15f).build();
        mGoogleMap.animateCamera(
                CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
    }

    private void enableMyLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude, longitude)).zoom(15f).build();

                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onFinish() {

                        }
                    });

                }

            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        setLocationData(latLng);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        ) {

            mGoogleMap.setMyLocationEnabled(true);

            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableMyLocation();
            }else{
                Toast.makeText(context, "Can't get current location. GPS is disabled", Toast.LENGTH_SHORT).show();
            }

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    permissionRequestFineLocation
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == permissionRequestFineLocation){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            ) {

                mGoogleMap.setMyLocationEnabled(true);

                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    enableMyLocation();
                }else{
                    Toast.makeText(context, "Can't get current location. GPS is disabled", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}