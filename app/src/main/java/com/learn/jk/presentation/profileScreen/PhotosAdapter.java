package com.learn.jk.presentation.profileScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.learn.jk.Application;
import com.learn.jk.R;
import com.learn.jk.databinding.PhotosItemBinding;
import com.learn.jk.presentation.homeScreen.IPostAdapterItemClickListener;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private List<String> mList;
    private IPostAdapterItemClickListener listener;

    public PhotosAdapter(List<String> mList, IPostAdapterItemClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhotosItemBinding itemBinding = PhotosItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String model = mList.get(position);
        RequestOptions options = RequestOptions
                .fitCenterTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(holder.ivPhoto.getWidth(), holder.ivPhoto.getHeight());
        Glide.with(Application.appContext)
                .load(model)
                .error(R.drawable.ic_round_error_outline_24)
                .thumbnail(0.5f)
                .apply(options)
                .into(holder.ivPhoto);

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        public ViewHolder(PhotosItemBinding binding) {
            super(binding.getRoot());
            ivPhoto = binding.ivSimplePostItem;
        }
    }
}

