package com.learn.jk.presentation.profileScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.jk.databinding.InformationItemBinding;

import java.util.Map;

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {
    private Map<String, String> mMap;
    private IInformationItemListener listener;

    public InformationAdapter(Map<String, String> mMap, IInformationItemListener listener) {
        this.mMap = mMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InformationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        InformationItemBinding itemBinding = InformationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new InformationAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull InformationAdapter.ViewHolder holder, int position) {
        Object[] keys = mMap.keySet().toArray();
        String title = keys[position].toString();
        String itemText = mMap.get(title);
        boolean checkIfMe = listener.isMyProfile();
        holder.isDeleteIconVisible(checkIfMe);
        holder.tvText.setText(itemText);
        holder.tvTitle.setText(title + ": ");
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnDeleteClickListener(title);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mMap.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvText;
        ImageView ivDelete;

        public ViewHolder(InformationItemBinding binding) {
            super(binding.getRoot());
            tvTitle = binding.informationTitle;
            tvText = binding.informationText;
            ivDelete = binding.ivDelete;
        }

        void isDeleteIconVisible(boolean isVisible) {
            if (isVisible)
                ivDelete.setVisibility(View.VISIBLE);
            else
                ivDelete.setVisibility(View.GONE);
        }
    }
}

