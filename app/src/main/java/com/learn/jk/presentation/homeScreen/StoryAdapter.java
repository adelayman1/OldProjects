package com.learn.jk.presentation.homeScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.jk.R;
import com.learn.jk.data.model.StoryModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.StoryItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private List<StoryModel> mList;
    private IStoryItemClickListener listener;

    public StoryAdapter(List<StoryModel> mList, IStoryItemClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StoryItemBinding itemBinding = StoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StoryAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.ViewHolder holder, int position) {
        StoryModel model = mList.get(position);
        holder.circularLine.setPortionsCount((int) model.getNum());
        FirebaseDatabase.getInstance().getReference().child("User").child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (!user.getPhoto().equals("noPhoto"))
                    Picasso.get().load(user.getPhoto()).error(R.drawable.ic_round_error_outline_24).into(holder.ivCircle);
                else
                    holder.ivCircle.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
                holder.tvUserName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.storyOnClick(model.getUid());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCircle;
        CircularStatusView circularLine;
        TextView tvUserName;

        public ViewHolder(StoryItemBinding binding) {
            super(binding.getRoot());
            ivCircle = binding.ivCircleStory;
            circularLine = binding.statuesLine;
            tvUserName = binding.tvStoryUserName;
        }
    }
}

