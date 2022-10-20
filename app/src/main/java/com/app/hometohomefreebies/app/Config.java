package com.app.hometohomefreebies.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.hometohomefreebies.R;
import com.app.hometohomefreebies.model.User;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Config {

    public static final String BASE_URL = "http://hometohomefreebies.solidbundle.com/api/v1/";

    public static User user = new User();

    public static String token = "";

    public static MultipartBody.Part prepareFilePart(String partName, String filePath) {

        //----------Prepare File Part---------------//
        File imageFile  = new File(filePath);

        RequestBody filePart = RequestBody.create(
                imageFile,
                MediaType.parse("file/*")
        );

        return MultipartBody.Part.createFormData(partName,
                "img." + filePath.substring(filePath.lastIndexOf(".")),
                filePart);
    }

    public static void pickImgFromGallery(Activity activity, int permissionRequest) {
        if(checkStoragePermission(activity, permissionRequest)){
            Matisse.from(activity)
                    .choose(MimeType.ofImage(), false)
                    .countable(false)
                    .maxSelectable(1)
                    .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showSingleMediaType(true)
                    .forResult(permissionRequest);
        }
    }

    public static void pickImagesFromGallery(Activity activity, int permissionRequest) {
        if(checkStoragePermission(activity, permissionRequest)){
            Matisse.from(activity)
                    .choose(MimeType.ofImage(), false)
                    .countable(false)
                    .maxSelectable(8)
                    .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showSingleMediaType(true)
                    .forResult(permissionRequest);
        }
    }

    private static Boolean checkStoragePermission(Activity activity, int permissionRequest){
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionRequest
            );

            return false;

        }

        return true;
    }

    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
