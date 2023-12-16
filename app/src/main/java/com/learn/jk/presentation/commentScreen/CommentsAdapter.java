package com.learn.jk.presentation.commentScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.jk.R;
import com.learn.jk.data.model.CommentModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.CommentItemBinding;
import com.learn.jk.presentation.homeScreen.IPostAdapterItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<CommentModel> mList;
    private IPostAdapterItemClickListener listener;

    public CommentsAdapter(List<CommentModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding itemBinding = CommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentsAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        CommentModel model = mList.get(position);
        holder.tvComment.setText(model.getCommentText());
        FirebaseDatabase.getInstance().getReference().child("User").child(model.getUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                holder.tvUserName.setText(userModel.getName());
                holder.ivVerified.setVisibility(userModel.isVerified() ? View.VISIBLE : View.GONE);
                Picasso.get().load(userModel.getPhoto()).error(R.drawable.ic_round_error_outline_24).into(holder.ivAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        ImageView ivVerified;
        TextView tvUserName;
        TextView tvComment;

        public ViewHolder(CommentItemBinding binding) {
            super(binding.getRoot());
            ivAvatar = binding.ivCommentAvatar;
            ivVerified = binding.ivAccountVerifiedComment;
            tvUserName = binding.tvUserNameComment;
            tvComment = binding.tvCommentText;
        }
    }
}

