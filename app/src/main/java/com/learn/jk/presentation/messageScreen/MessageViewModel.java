package com.learn.jk.presentation.messageScreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.jk.data.repository.MessageRepository;
import com.learn.jk.data.repository.Repository;
import com.learn.jk.data.repository.UserRepository;
import com.learn.jk.data.model.MessageModel;
import com.learn.jk.data.model.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.observers.ResourceSingleObserver;

public class MessageViewModel extends ViewModel {
    private Repository repository = Repository.getInstance();
    private MessageRepository messageRepository = MessageRepository.getInstance();
    private UserRepository userRepository = UserRepository.getInstance();
    private String myUID = repository.getUid();
    public MutableLiveData<List<MessageModel>> mMessageList;
    public MutableLiveData<List<UserModel>> mChatsList;
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void sendMessage(String userID, String message) {
        messageRepository.sendMessage(userID, myUID, message);
    }

    public LiveData<List<MessageModel>> getMessages(String userID) {
        if (mMessageList == null) {
            mMessageList = new MutableLiveData<>();
            loadMessages(userID);
        }
        return mMessageList;
    }

    public void loadMessages(String userID) {
        messageRepository.getMessages(userID, myUID).subscribeWith(new DisposableObserver<List<MessageModel>>() {
            @Override
            public void onNext(@NonNull List<MessageModel> messageModels) {
                mMessageList.postValue(messageModels);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }

    /*public void getRecentChats() {

        List<String> mChatsUIDs = new ArrayList<>();
        List<UserModel> mChats = new ArrayList<>();
        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChats.clear();
                for (String item : mChatsUIDs) {
                    UserModel user = snapshot.child(item).getValue(UserModel.class);
                    mChats.add(user);
                }
                mChatsList.postValue(mChats);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        repository.getMessage().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChatsUIDs.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    MessageModel model = item.getValue(MessageModel.class);
                    assert model != null;
                    if (model.getReceiverUID().equals(myUID) && !mChatsUIDs.contains(model.getSenderUID())) {
                        String uid = model.getSenderUID();
                        mChatsUIDs.add(uid);
                    } else if (model.getSenderUID().equals(myUID) && !mChatsUIDs.contains(model.getReceiverUID())) {
                        String uid = model.getReceiverUID();
                        mChatsUIDs.add(uid);

                    }
                }
                repository.getUsers().addListenerForSingleValueEvent(usersListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
    //TODO
    //TODO:MAKE WITH RXJAVA
    //this method get people you have chatted with them
    public LiveData<List<UserModel>> getRecentChats() {
        if (mChatsList == null) {
            mChatsList = new MutableLiveData<>();
            loadRecentChats();
        }
        return mChatsList;
    }

    public void loadRecentChats() {
        List<UserModel> temp = new ArrayList<>();
        messageRepository.getRecentUsersChatsUIDs(myUID).subscribeWith(new ResourceSingleObserver<List<String>>() {
            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<String> strings) {
                strings.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {

                        userRepository.getUser(s).subscribeWith(new DisposableSingleObserver<UserModel>() {
                            @Override
                            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull UserModel userModel) {
                                temp.add(userModel);
                                mChatsList.postValue(temp);
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            }
                        });
                    }
                });
                isLoading.postValue(false);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }
}
