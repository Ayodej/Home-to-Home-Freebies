package com.app.hometohomefreebies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.databinding.LayoutAddImageItemBinding;
import com.app.hometohomefreebies.databinding.LayoutImageItemBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class AddImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = AddImageAdapter.class.getSimpleName();
    private Context mContext;
    private List<String> imageList;

    private final int ADD_IMAGE_LAYOUT = 1;
    private final int IMAGE_LAYOUT = 2;

    private Interaction interaction;

    public AddImageAdapter(Context mContext, List<String> imageList, Interaction interaction) {
        this.mContext = mContext;
        this.imageList = imageList;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ADD_IMAGE_LAYOUT){
            LayoutAddImageItemBinding binding = LayoutAddImageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new AddImageHolder(binding);
        }
        LayoutImageItemBinding binding = LayoutImageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType() == ADD_IMAGE_LAYOUT){
            AddImageHolder addImageHolder = (AddImageHolder) holder;
            addImageHolder.setData(imageList.get(position));
        }else{
            ImageHolder imageHolder = (ImageHolder) holder;
            imageHolder.setData(position, imageList.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(imageList.get(position).isEmpty()){
            return ADD_IMAGE_LAYOUT;
        }
        return IMAGE_LAYOUT;
    }

    public class AddImageHolder extends RecyclerView.ViewHolder{

        LayoutAddImageItemBinding mBinding;

        public AddImageHolder(LayoutAddImageItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setData(String image){
            mBinding.ibAdd.setOnClickListener(view -> {
                interaction.onAddImageClicked();
            });
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder{

        LayoutImageItemBinding mBinding;

        public ImageHolder(LayoutImageItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setData(int position, String image){
            Glide.with(mContext)
                    .load(image)
                    .into(mBinding.ivImg);

            mBinding.ibClose.setOnClickListener(view -> interaction.onDeleteImageClicked(position));
        }
    }

    public interface Interaction {
        void onAddImageClicked();
        void onDeleteImageClicked(int position);
    }

}
