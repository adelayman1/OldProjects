package com.learn.jk.presentation.searchScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.jk.R;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.SearchItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<UserModel> mUsers;
    private SearchAdapterItemClickListener listener;

    public UsersAdapter(List<UserModel> mUsers, SearchAdapterItemClickListener listener) {
        this.mUsers = mUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemBinding itemBinding = SearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UsersAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersAdapter.ViewHolder holder, int position) {
        final UserModel model = (UserModel) mUsers.get(position);
        holder.tvName.setText(model.getName());
        holder.tvLink.setText("https://" + model.getKey());
        if (!model.getPhoto().equals("noPhoto"))
            Picasso.get().load(model.getPhoto()).error(R.drawable.ic_round_error_outline_24).into(holder.ivAvatar);
        else
            holder.ivAvatar.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.itemClickListener(model.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvLink;
        ImageView ivAvatar;

        public ViewHolder(SearchItemBinding binding) {
            super(binding.getRoot());
            tvName = binding.tvNameSearch;
            tvLink = binding.tvLinkSearch;
            ivAvatar = binding.ivAvatarSearch;
        }
    }
}
