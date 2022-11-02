package com.app.hometohomefreebies.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.hometohomefreebies.adapter.ChatsAdapter;
import com.app.hometohomefreebies.databinding.ActivityChatBinding;
import com.app.hometohomefreebies.model.Chat;
import com.app.hometohomefreebies.model.Product;
import com.app.hometohomefreebies.network.ApiClient;
import com.app.hometohomefreebies.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ChatActivity extends AppCompatActivity implements ChatsAdapter.Interaction{

    private final Context context = ChatActivity.this;

    private ActivityChatBinding binding;

    private final ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    private ChatsAdapter chatsAdapter;

    private List<Chat> chatList = new ArrayList<>();

    private List<Chat> chatListBackup = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initListeners();
        initRecyclerView();
        getChats();
    }

    private void initListeners(){
        binding.ibBack.setOnClickListener(view -> finish());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()){
                    chatList.clear();
                    for (Chat chat : chatListBackup){

                        String name = chat.getUsers().get(0).getFirstName().toLowerCase() + " " + chat.getUsers().get(0).getLastName().toLowerCase();

                        if(name.contains(charSequence.toString().toLowerCase())){
                            chatList.add(chat);
                        }
                    }
                    chatsAdapter.notifyDataSetChanged();
                }else{
                    chatList.clear();
                    chatList.addAll(chatListBackup);
                    chatsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        binding.etSearch.addTextChangedListener(textWatcher);
    }

    private void initRecyclerView(){
        chatsAdapter = new ChatsAdapter(context, chatList, this);
        binding.rvChats.setLayoutManager(new LinearLayoutManager(context));
        binding.rvChats.setHasFixedSize(true);
        binding.rvChats.setItemAnimator(null);
        binding.rvChats.setAdapter(chatsAdapter);
    }

    @SuppressLint("CheckResult")
    private void getChats(){
        apiService
                .getMyChats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Chat>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Chat> c) {
                        chatList.clear();
                        chatListBackup.clear();
                        chatList.addAll(c);
                        chatListBackup.addAll(c);
                        chatsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    public void onChatClicked(Chat chat) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra("chat", chat);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChats();
    }
}