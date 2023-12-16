package com.learn.jk.presentation.messageScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.jk.R;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.ChatItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<UserModel> mList;
    private IChatItemListener listener;

    public ChatAdapter(List<UserModel> mList, IChatItemListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatItemBinding itemBinding = ChatItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        UserModel model = mList.get(position);
        holder.tvName.setText(model.getName());
        if (!model.getPhoto().equals("noPhoto"))
            Picasso.get().load(model.getPhoto()).into(holder.ivAvatar);
        else
            holder.ivAvatar.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnChatItemClickListener(model);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;

        public ViewHolder(ChatItemBinding binding) {
            super(binding.getRoot());
            ivAvatar = binding.ivUserChatAvatar;
            tvName = binding.tvUserChatName;
        }
    }
}



