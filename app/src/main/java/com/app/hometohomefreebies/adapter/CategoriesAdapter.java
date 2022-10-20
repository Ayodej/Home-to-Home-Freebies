package com.app.hometohomefreebies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.databinding.LayoutCategoryItemBinding;
import com.app.hometohomefreebies.model.Category;
import com.app.hometohomefreebies.model.Product;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = CategoriesAdapter.class.getSimpleName();
    private Context mContext;
    private List<Category> categoryList;

    Interaction interaction;

    public CategoriesAdapter(Context mContext, List<Category> categoryList, Interaction interaction) {
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutCategoryItemBinding binding = LayoutCategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        CategoryHolder categoryHolder = (CategoryHolder) holder;
        final Category category = categoryList.get(position);
        categoryHolder.setData(category);
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class CategoryHolder extends RecyclerView.ViewHolder{

        LayoutCategoryItemBinding mBinding;

        public CategoryHolder(LayoutCategoryItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setData(Category category){
            Glide.with(mContext)
                    .load(category.getImage())
                    .into(mBinding.ivImg);

            mBinding.tvTitle.setText(category.getTitle());
            mBinding.tvCount.setText(category.getProductCount() + " " + "items");

            mBinding.getRoot().setOnClickListener(view -> interaction.onCategoryClicked(category));
        }
    }

    public interface Interaction {
        void onCategoryClicked(Category category);
    }

}
