package com.learn.jk.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Repository {
    public static Repository instance;
    String uid;
    public DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public static Repository getInstance() {
        if (instance != null)
            return instance;

        instance = new Repository();
        return instance;
    }

    /**
     * this code add hash tag to firebase
     *
     * @param postKey a key of post contains hashTags
     * @param hashTag a hashTag in post
     */
    public void addHashTag(String postKey, String hashTag) {
        reference.child("Tags").child(hashTag).child(postKey).setValue("true"); // add hashTag to db
    }

    /**
     * @param email a user uid we will send to him password reset email
     * @return the {@link Task} for this operation
     */
    public Task<Void> forgetPassword(String email) {
        return FirebaseAuth.getInstance().sendPasswordResetEmail(email);
    }

    public DatabaseReference getAppVersion() {
        return reference.child("Global").child("AppVersion");
    }

    public DatabaseReference getAdminMessages() {
        return reference.child("Global").child("Messages");
    }

    public DatabaseReference isAppStop() {
        return reference.child("Global").child("stop");
    }

    public synchronized StorageReference uploadPhoto() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        return storageRef.child("images").child(UUID.randomUUID().toString() + UUID.randomUUID().toString());
    }

    public Task<AuthResult> signIn(String email, String password) {
        final MutableLiveData<String> result = new MutableLiveData<>();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signUp(String email, String password, String first, String last, String date) {
        final MutableLiveData<String> result = new MutableLiveData<>();

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();


        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public synchronized Query getPosts(String userUID) {
        Query postsQuery;
        postsQuery = reference.child("Posts").orderByChild("uid").equalTo(userUID);
        return postsQuery;
    }

    public synchronized Query getPost(String userUID) {
        Query postQuery;
        postQuery = reference.child("Posts").child(userUID);
        return postQuery;
    }

    public synchronized DatabaseReference getPosts() {
        DatabaseReference postsReference;
        postsReference = reference.child("Posts");
        return postsReference;
    }

    public synchronized String getUid() {
        if (uid == null) uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return uid;
    }

    public synchronized boolean isLogin() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void like(String key, String uid) {
        reference.child("Likes").child(key).child(uid).setValue("true");
    }

    public void unlike(String key, String uid) {
        reference.child("Likes").child(key).child(uid).removeValue();
    }

    public synchronized DatabaseReference getLike(String key) {
        return reference.child("Likes").child(key);
    }

    public synchronized DatabaseReference getUser(String uid) {
        return reference.child("User").child(uid);
    }

    public synchronized DatabaseReference getUsers() {
        return reference.child("User");
    }

    public synchronized DatabaseReference getTagByName(String tagName) {
        return reference.child("Tags").child(tagName);
    }

    public synchronized DatabaseReference getTags() {
        return reference.child("Tags");
    }

    public synchronized DatabaseReference getFollowersByUID(String uid) {
        return reference.child("Followers").child(uid);
    }

    public synchronized LiveData<HashMap<String, Object>> sendPost(String uid, HashMap<String, Object> data) {
        MutableLiveData<HashMap<String, Object>> returnMap = new MutableLiveData<>();
        DatabaseReference postReference = reference.child("Posts").push();
        data.put("key", postReference.getKey());
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", postReference.getKey());
        map.put("listener", postReference.setValue(data));
        returnMap.postValue(map);
        return returnMap;
    }

    public void follow(String myUid, String uid) {
        reference.child("Followers").child(uid).child(myUid).setValue("t");
        reference.child("Following").child(myUid).child(uid).setValue("t");
    }

    public void unFollow(String myUid, String uid) {
        reference.child("Followers").child(uid).child(myUid).removeValue();
        reference.child("Following").child(myUid).child(uid).removeValue();
    }

    public synchronized DatabaseReference getFollowing(String myUid) {
        DatabaseReference following = reference.child("Following").child(myUid);
        return following;
    }

    public synchronized Task<Void> changeUserData(String myUid, HashMap<String, Object> data) {

        return reference.child("User").child(myUid).updateChildren(data);
    }

    public synchronized DatabaseReference getComments(String PostKey) {

        return reference.child("Comments").child(PostKey);
    }

    /**
     * @param postKey the key of post user commented to it
     * @param data    {@Link HashMap} of data
     * @return the {@link Task} for this operation
     */
    public synchronized Task<Void> sendComments(String postKey, HashMap<String, String> data) {
        DatabaseReference commentReference = reference.child("Comments").child(postKey).push();
        data.put("key", commentReference.getKey());
        return commentReference.setValue(data);
    }

    /**
     * @param uid      uid of the user added the story
     * @param imageURL image URL of storyImage
     * @return the {@link Task} for this operation
     */
    public synchronized Task<Void> addStory(String uid, String imageURL) {
        DatabaseReference storyReference = reference.child("Story").child(uid).push();
        return storyReference.setValue(imageURL);
    }

    /**
     * @param uid a user uid we will get his stories
     * @return DatabaseReference for user stories
     */
    public synchronized DatabaseReference getStory(String uid) {
        return reference.child("Story").child(uid);
    }

    /**
     * @param receiverUID a user uid we will send to him message
     * @param senderUID   message sender uid (my uid)
     * @param message     messageText we will send it
     * @return the {@link Task} for this operation
     */
    public synchronized Task<Void> sendMessage(String receiverUID, String senderUID, String message) {
        DatabaseReference messagesReference = reference.child("Messages").push();
        Map<String, String> data = new HashMap<>();
        data.put("key", messagesReference.getKey());
        data.put("text", message);
        data.put("senderUID", senderUID);
        data.put("receiverUID", receiverUID);
        return messagesReference.setValue(data);
    }

    /**
     * @return the {@link DatabaseReference} for all messages
     */
    public synchronized DatabaseReference getMessage() {
        return reference.child("Messages");
    }
}
