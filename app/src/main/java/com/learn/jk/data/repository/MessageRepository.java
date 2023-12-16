package com.learn.jk.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.jk.data.model.MessageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;

//TODO TRUE
//TODO TRUE
//TODO TRUE
public class MessageRepository {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    public static MessageRepository instance;

    public static MessageRepository getInstance() {
        if (instance != null)
            return instance;

        instance = new MessageRepository();
        return instance;
    }

    /**
     * @param receiverUID a user uid we will send to him message
     * @param senderUID   message sender uid (my uid)
     * @param message     messageText we will send it
     * @return the {@link Task} for this operation
     */
    public void sendMessage(String receiverUID, String senderUID, String message) {
        DatabaseReference messagesReference = reference.child("Messages").push();
        Map<String, String> data = new HashMap<>();
        data.put("key", messagesReference.getKey());
        data.put("text", message);
        data.put("senderUID", senderUID);
        data.put("receiverUID", receiverUID);
        messagesReference.setValue(data);
    }

    /**
     * @return the {@link DatabaseReference} for all messages
     */
    public Observable<List<MessageModel>> getMessages(String userUID, String myUID) {
        return Observable.create(new ObservableOnSubscribe<List<MessageModel>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<List<MessageModel>> emitter) throws Throwable {
                List<MessageModel> tempMessagesList = new ArrayList<>();
                reference.child("Messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tempMessagesList.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            MessageModel model = item.getValue(MessageModel.class);
                            if ((model.getReceiverUID().equals(userUID) && model.getSenderUID().equals(myUID)) || model.getSenderUID().equals(userUID) && model.getReceiverUID().equals(myUID))
                                tempMessagesList.add(model);
                        }
                        emitter.onNext(tempMessagesList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }
    public Single<List<String>> getRecentUsersChatsUIDs(String myUID) {
        List<String> tempUsersUIDs = new ArrayList<>();
        return Single.create(new SingleOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull SingleEmitter<List<String>> emitter) throws Throwable {

                reference.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tempUsersUIDs.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            if(emitter.isDisposed())
                                return;
                            MessageModel model = item.getValue(MessageModel.class);
                            if (model.getReceiverUID().equals(myUID) && !tempUsersUIDs.contains(model.getSenderUID())) {
                                String uid = model.getSenderUID();
                                tempUsersUIDs.add(uid);
                            } else if (model.getSenderUID().equals(myUID) && !tempUsersUIDs.contains(model.getReceiverUID())) {
                                String uid = model.getReceiverUID();
                                tempUsersUIDs.add(uid);
                            }
                        }
                        emitter.onSuccess(tempUsersUIDs);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        emitter.onError(error.toException());
                    }
                });
            }
        });
    }

}
