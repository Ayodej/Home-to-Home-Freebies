package com.app.hometohomefreebies.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.app.Config;
import com.app.hometohomefreebies.databinding.LayoutChatItemBinding;
import com.app.hometohomefreebies.databinding.LayoutMessageRowIncomingBinding;
import com.app.hometohomefreebies.databinding.LayoutMessageRowOutgoingBinding;
import com.app.hometohomefreebies.model.Chat;
import com.app.hometohomefreebies.model.Message;
import com.app.hometohomefreebies.model.User;
import com.bumptech.glide.Glide;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = MessagesAdapter.class.getSimpleName();
    private Context mContext;
    private List<Message> messageList;
    private User user;

    Interaction interaction;

    private final int incomingMessage = 1;
    private final int outgoingMessage = 2;

    public MessagesAdapter(Context mContext, List<Message> messageList, User user, Interaction interaction) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.user = user;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == incomingMessage){
            LayoutMessageRowIncomingBinding binding = LayoutMessageRowIncomingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MessageHolder(binding);
        }

        LayoutMessageRowOutgoingBinding binding = LayoutMessageRowOutgoingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyMessageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(holder.getItemViewType() == incomingMessage){
            MessageHolder messageHolder = (MessageHolder) holder;
            final Message message = messageList.get(position);
            messageHolder.setData(message);
        }

        if(holder.getItemViewType() == outgoingMessage){
            MyMessageHolder myMessageHolder = (MyMessageHolder) holder;
            final Message message = messageList.get(position);
            myMessageHolder.setData(message);
        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getSenderId() == Config.user.getId()){
            return outgoingMessage;
        }
        return incomingMessage;
    }

    public class MessageHolder extends RecyclerView.ViewHolder{

        LayoutMessageRowIncomingBinding mBinding;

        public MessageHolder(LayoutMessageRowIncomingBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void setData(Message message){
            if(!user.getImage().isEmpty()){
                Glide.with(mContext)
                        .load(user.getImage())
                        .into(mBinding.userImg);
            }
            mBinding.txtMessage.setText(message.getMessage());
        }
    }

    public class MyMessageHolder extends RecyclerView.ViewHolder{

        LayoutMessageRowOutgoingBinding mBinding;

        public MyMessageHolder(LayoutMessageRowOutgoingBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void setData(Message message){
            mBinding.txtMessage.setText(message.getMessage());
        }
    }

    public interface Interaction {
    }

}
