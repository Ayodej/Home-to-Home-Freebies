package com.app.hometohomefreebies.network;

import com.app.hometohomefreebies.model.Authorize;
import com.app.hometohomefreebies.model.Category;
import com.app.hometohomefreebies.model.Chat;
import com.app.hometohomefreebies.model.Product;
import com.app.hometohomefreebies.model.User;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface ApiService {

    @Multipart
    @POST("register")
    Single<Authorize> registerUser(
            @Part("first_name") String firstName,
            @Part("last_name") String lastName,
            @Part("email") String email,
            @Part("password") String password,
            @Part("phone") String phone,
            @Part MultipartBody.Part image,
            @Part("notification_token") String notificationToken,
            @Part("device_id") String deviceId
    );

    @Multipart
    @POST("login")
    Single<Authorize> loginUser(
            @Part("email") String email,
            @Part("password") String password,
            @Part("notification_token") String notificationToken,
            @Part("device_id") String deviceId
    );

    @Multipart
    @POST("update-profile")
    Single<Authorize> editProfile(
            @Part("first_name") String firstName,
            @Part("last_name") String lastName,
            @Part("phone") String phone,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("me")
    Single<User> myData(
            @Part("notification_token") String notificationToken,
            @Part("device_id") String deviceId
    );

    @GET("category")
    Single<List<Category>> getCategories();

    @GET("product")
    Single<List<Product>> getProducts();

    @Multipart
    @POST("product")
    Completable addProduct(
            @Part("title") String title,
            @Part("description") String description,
            @Part("address") String address,
            @Part("latitude") Double latitude,
            @Part("longitude") Double longitude,
            @Part("category_id") int categoryId,
            @Part List<MultipartBody.Part> images
    );

    @Multipart
    @POST("products-by-category-id")
    Single<List<Product>> getProductsByCategoryId(
            @Part("category_id") int categoryId
    );

    @Multipart
    @POST("search")
    Single<List<Product>> search(
            @Part("query") String query
    );

    @Multipart
    @POST("delete-product")
    Completable deleteProduct(
            @Part("product_id") int productId
    );

    //=================Start Chat====================//

    @GET("chat/my-chats")
    Single<List<Chat>> getMyChats();

    @GET("chat/{chatId}/read")
    Completable readChat(
            @Path("chatId") int chatId
    );

    @Multipart
    @POST("chat/by-user")
    Single<Chat> getChatByUser(
            @Part("user_id") int userId
    );

    @Multipart
    @POST("chat/create")
    Single<Chat> createChat(
            @Part("user_id") int userId
    );

    //=================End Chat====================//

    //=================Start Message====================//

    @Multipart
    @POST("message/create")
    Completable createMessage(
            @Part("message") String message,
            @Part("type") String type,
            @Part("chat_id") int chatId
    );

    //=================End Message====================//

}
