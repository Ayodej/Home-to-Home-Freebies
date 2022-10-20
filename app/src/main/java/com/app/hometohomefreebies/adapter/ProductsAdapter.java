package com.app.hometohomefreebies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hometohomefreebies.databinding.LayoutCategoryItemBinding;
import com.app.hometohomefreebies.databinding.LayoutProductItemBinding;
import com.app.hometohomefreebies.model.Category;
import com.app.hometohomefreebies.model.Product;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = ProductsAdapter.class.getSimpleName();
    private Context mContext;
    private List<Product> productList;

    private Interaction interaction;

    public ProductsAdapter(Context mContext, List<Product> productList, Interaction interaction) {
        this.mContext = mContext;
        this.productList = productList;
        this.interaction = interaction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutProductItemBinding binding = LayoutProductItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ProductHolder productHolder = (ProductHolder) holder;
        final Product product = productList.get(position);
        productHolder.setData(product);
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class ProductHolder extends RecyclerView.ViewHolder{

        LayoutProductItemBinding mBinding;

        public ProductHolder(LayoutProductItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setData(Product product){
            Glide.with(mContext)
                    .load(product.getImageList().get(0).getPath())
                    .into(mBinding.ivImg);

            mBinding.tvTitle.setText(product.getTitle());
            mBinding.tvUsername.setText(product.getUser().getFirstName() + " " + product.getUser().getLastName());
            mBinding.tvLocation.setText(product.getAddress());
            mBinding.tvPhoneNumber.setText(product.getUser().getPhone());

            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    interaction.onProductClicked(product);
                }
            });
        }
    }

    public interface Interaction {
        void onProductClicked(Product product);
    }
}
