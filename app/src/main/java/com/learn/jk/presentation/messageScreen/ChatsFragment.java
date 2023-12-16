package com.learn.jk.presentation.messageScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.jk.data.model.MessageModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.FragmentChatsBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment implements IChatItemListener {
    private RecyclerView recyclerView;
    private MessageViewModel viewModel;
    private ChatAdapter adapter;
    private List<UserModel> mChatsList;
    private FragmentChatsBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(MessageViewModel.class);
        mChatsList = new ArrayList<>();
        recyclerView = binding.rcChats;
        adapter = new ChatAdapter(mChatsList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        viewModel.getRecentChats().observe(getViewLifecycleOwner(), new Observer<List<UserModel>>() {
            @Override
            public void onChanged(List<UserModel> userModels) {
                if (userModels.size() <= 0) {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    return;
                }
                binding.tvNoData.setVisibility(View.GONE);
                mChatsList.clear();
                mChatsList.addAll(userModels);
                adapter.notifyDataSetChanged();
                binding.svChatScroll.setVisibility(View.VISIBLE);
            }
        });
        viewModel.isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.progressbarChats.setVisibility(View.VISIBLE);
                    binding.svChatScroll.setVisibility(View.GONE);
                    binding.tvNoData.setVisibility(View.GONE);
                } else {
                    binding.progressbarChats.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void OnChatItemClickListener(UserModel user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("uid", user.getKey());
        intent.putExtra("image", user.getPhoto());
        intent.putExtra("name", user.getName());
        startActivity(intent);
    }
}