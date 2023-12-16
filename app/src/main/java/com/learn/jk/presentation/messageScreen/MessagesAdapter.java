package com.learn.jk.presentation.messageScreen;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.jk.data.model.MessageModel;
import com.learn.jk.databinding.MessageItemBinding;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<MessageModel> mList;
    private String UserUID;
    private String UserName;

    public MessagesAdapter(List<MessageModel> mList, String UserUID, String UserName) {
        this.mList = mList;
        this.UserUID = UserUID;
        this.UserName = UserName;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageItemBinding itemBinding = MessageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel model = mList.get(position);
        if (model.getSenderUID().equals(UserUID)) {
            holder.tvUserName.setText(UserName);
        } else {
            holder.tvUserName.setText("You");
        }
        holder.tvMessage.setText(model.getText());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvMessage;

        public ViewHolder(MessageItemBinding binding) {
            super(binding.getRoot());
            tvUserName = binding.tvUserNameMessage;
            tvMessage = binding.tvMessage;
        }
    }
}

