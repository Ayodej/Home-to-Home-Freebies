package com.app.hometohomefreebies.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.databinding.LayoutCategoryItemBinding;
import com.app.hometohomefreebies.databinding.LayoutChatItemBinding;
import com.app.hometohomefreebies.model.Category;
import com.app.hometohomefreebies.model.Chat;
import com.app.hometohomefreebies.model.Message;
import com.bumptech.glide.Glide;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = ChatsAdapter.class.getSimpleName();
    private Context mContext;
    private List<Chat> chatList;

    Interaction interaction;

    public ChatsAdapter(Context mContext, List<Chat> chatList, Interaction interaction) {
        this.mContext = mContext;
        this.chatList = chatList;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutChatItemBinding binding = LayoutChatItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ChatHolder chatHolder = (ChatHolder) holder;
        final Chat chat = chatList.get(position);
        chatHolder.setData(chat);
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ChatHolder extends RecyclerView.ViewHolder{

        LayoutChatItemBinding mBinding;

        public ChatHolder(LayoutChatItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void setData(Chat chat){

            if(!chat.getUsers().isEmpty()){
                if(!chat.getUsers().get(0).getImage().isEmpty()){
                    Glide.with(mContext)
                            .load(chat.getUsers().get(0).getImage())
                            .into(mBinding.ivUserImg);
                }
                mBinding.tvFullName.setText(chat.getUsers().get(0).getFirstName() + " " + chat.getUsers().get(0).getLastName());
            }

            if(!chat.getMessages().isEmpty()){
                mBinding.tvLastMessage.setText(chat.getMessages().get(chat.getMessages().size()-1).getMessage());
                mBinding.tvTime.setText(chat.getMessages().get(chat.getMessages().size()-1).getCreatedAt());
            }

            int unReadMessagesCount = 0;
            for (Message message:
                 chat.getMessages()) {
                if(!message.isRead()){
                    unReadMessagesCount+=1;
                }
            }

            if(unReadMessagesCount == 0){
                mBinding.flUnReadMessagesCount.setVisibility(View.GONE);
            }else{
                mBinding.flUnReadMessagesCount.setVisibility(View.VISIBLE);
                mBinding.tvUnReadMessagesCount.setText("" + unReadMessagesCount);
            }


            mBinding.getRoot().setOnClickListener(view -> {
                mBinding.flUnReadMessagesCount.setVisibility(View.GONE);
                interaction.onChatClicked(chat);
            });
        }
    }

    public interface Interaction {
        void onChatClicked(Chat chat);
    }

}
