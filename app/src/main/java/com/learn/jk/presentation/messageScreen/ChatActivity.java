package com.learn.jk.presentation.messageScreen;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.learn.jk.R;
import com.learn.jk.data.model.MessageModel;
import com.learn.jk.databinding.ActivityChatBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private String userUID;
    private String image;
    private String name;
    private MessageViewModel viewModel;
    private List<MessageModel> mMessageList;
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChatBinding binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        //get data from Intent
        userUID = getIntent().getStringExtra("uid");
        image = getIntent().getStringExtra("image");
        name = getIntent().getStringExtra("name");
        mMessageList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(mMessageList, userUID, name);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.rcMessages.setAdapter(messagesAdapter);
        binding.rcMessages.setLayoutManager(layoutManager);
        viewModel.getMessages(userUID).observe(this, new Observer<List<MessageModel>>() {
            @Override
            public void onChanged(List<MessageModel> messageModels) {
                mMessageList.clear(); //TODO://CHECK IF THIS LINE TRUE
                mMessageList.addAll(messageModels);
                messagesAdapter.notifyDataSetChanged();
            }
        });
        ;
        binding.tvUserName.setText(name);
        if (!image.equals("noPhoto"))
            Picasso.get().load(image).into(binding.ivAvatar);
        else
            binding.ivAvatar.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);
        binding.tvUserName.setText(name);
        binding.ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etMessage.getText().toString().trim().isEmpty()) {
                    viewModel.sendMessage(userUID, binding.etMessage.getText().toString());
                    binding.etMessage.setText("");
                }
            }
        });
    }
}