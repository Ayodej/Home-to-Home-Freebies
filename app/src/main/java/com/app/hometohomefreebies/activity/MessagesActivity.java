package com.app.hometohomefreebies.activity;

import static com.app.hometohomefreebies.app.Config.pusher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.hometohomefreebies.adapter.MessagesAdapter;
import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.ActivityMessagesBinding;
import com.app.hometohomefreebies.model.Chat;
import com.app.hometohomefreebies.model.Message;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PusherEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MessagesActivity extends AppCompatActivity implements MessagesAdapter.Interaction{

    private final Context context = MessagesActivity.this;

    private ActivityMessagesBinding binding;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    private Chat chat;

    private MessagesAdapter messagesAdapter;

    private List<Message> messageList = new ArrayList<>();

    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        initRecyclerView();
        setData();
        initListeners();
    }

    private void getIntentData(){
        chat = (Chat) getIntent().getSerializableExtra("chat");

        if(chat.getId() != null){
            messageList.clear();
            messageList.addAll(chat.getMessages());
            initPusher();
            readChat();
        }else{
            getChatByUserId();
        }
    }

    private void setData(){
        if(!chat.getUsers().get(0).getImage().isEmpty()){
            Glide.with(context)
                    .load(chat.getUsers().get(0).getImage())
                    .into(binding.ivUserImg);
        }
        binding.tvFullName.setText(chat.getUsers().get(0).getFirstName() + " " + chat.getUsers().get(0).getLastName());
    }

    private void initListeners(){
        binding.ibBack.setOnClickListener(view -> finish());
        binding.flSendMessage.setOnClickListener(view -> {
            if(!binding.etMessage.getText().toString().isEmpty()){

                //Add Message to recyclerview
                message = binding.etMessage.getText().toString();
                messageList.add(new Message(0, binding.etMessage.getText().toString(),
                        "text", true, "", Config.user.getId(), "Just now"));
                messagesAdapter.notifyDataSetChanged();

                binding.etMessage.setText("");
                binding.rvMessages.scrollToPosition(messageList.size() - 1);

                ///==================//
                if(chat.getId() == null){ //Create Chat first
                    createChat();
                }else{
                    createMessage();
                }
            }
        });
    }

    private void initRecyclerView(){
        messagesAdapter = new MessagesAdapter(context, messageList, chat.getUsers().get(0), this);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(context));
        binding.rvMessages.setHasFixedSize(true);
        binding.rvMessages.setItemAnimator(null);
        binding.rvMessages.setAdapter(messagesAdapter);
        binding.rvMessages.scrollToPosition(messageList.size() - 1);
    }

    private void initPusher(){
        Channel messagesChannel = pusher.getChannel("chat." + chat.getId());
        if(messagesChannel == null){
            messagesChannel = pusher.subscribe("chat." + chat.getId());
        }

        messagesChannel.bind("new-message", new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d("MessagesActivity", channelName);
            }

            @Override
            public void onError(String message, Exception e) {
                ChannelEventListener.super.onError(message, e);
            }

            @Override
            public void onEvent(PusherEvent event) {
                Gson gson = new Gson();
                JsonObject gsonObject = gson.fromJson(event.getData(), JsonObject.class);
                JsonObject jsonObjectMessage = gsonObject.getAsJsonObject();

                Message message = new Message();
                message.setSenderId(jsonObjectMessage.get("user_id").getAsInt());

                if(message.getSenderId() != Config.user.getId()){
                    message.setId(jsonObjectMessage.get("id").getAsInt());
                    message.setMessage(jsonObjectMessage.get("message").getAsString());
                    message.setType(jsonObjectMessage.get("type").getAsString());
                    message.setCreatedAt(jsonObjectMessage.get("created_at").getAsString());
                    messageList.add(message);

                    runOnUiThread(() -> {
                        messagesAdapter.notifyItemInserted(messageList.size() - 1);
                        binding.rvMessages.scrollToPosition(messageList.size() - 1);
                        readChat();
                    });
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getChatByUserId(){
        apiService
                .getChatByUser(
                        chat.getUsers().get(0).getId()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Chat>() {

                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(Chat chat) {
                        MessagesActivity.this.chat = chat;
                        messageList.clear();
                        messageList.addAll(chat.getMessages());
                        messagesAdapter.notifyDataSetChanged();
                        binding.rvMessages.scrollToPosition(messageList.size() - 1);
                        initPusher();
                        readChat();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void readChat(){
        apiService
                .readChat(
                        chat.getId()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void createChat(){
        apiService
                .createChat(
                        chat.getUsers().get(0).getId()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Chat>() {

                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(Chat chat) {
                        MessagesActivity.this.chat = chat;
                        initPusher();
                        readChat();
                        createMessage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void createMessage(){
        apiService
                .createMessage(
                        message,
                        "text",
                        chat.getId()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}