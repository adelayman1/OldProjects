package com.learn.jk.presentation.homeScreen;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learn.jk.Application;
import com.learn.jk.R;
import com.learn.jk.data.GLOBAL;
import com.learn.jk.data.repository.Repository;
import com.learn.jk.data.model.PostModel;
import com.learn.jk.data.model.UserModel;
import com.learn.jk.databinding.PostItemBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostModel> mPosts;
    private String uid = Repository.getInstance().getUid();
    private IPostAdapterItemClickListener listener;
    private Lifecycle lifecycle;
    private HashTagHelper mTextHashTagHelper;

    public PostAdapter(List<PostModel> mPosts, IPostAdapterItemClickListener listener, Lifecycle lifecycle) {
        this.mPosts = mPosts;
        this.listener = listener;
        this.lifecycle = lifecycle;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostItemBinding itemBinding = PostItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, int position) {
        final UserModel[] userModel = new UserModel[1];
        final PostModel postModel = mPosts.get(position);
        RequestOptions options = RequestOptions
                .fitCenterTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(holder.ivPost.getWidth(), holder.ivPost.getHeight());
        mTextHashTagHelper = HashTagHelper.Creator.create(Color.parseColor("#72a9e7"), new HashTagHelper.OnHashTagClickListener() {
            @Override
            public void onHashTagClicked(String hashTag) {
                listener.tagClickListener(hashTag);
            }
        }, '_');
        mTextHashTagHelper.handle(holder.tvHint);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(postModel.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel[0] = snapshot.getValue(UserModel.class);
                holder.ivUserVerified.setVisibility(userModel[0].isVerified() ? View.VISIBLE : View.GONE);
                holder.tvName.setText(userModel[0].getName());
                if (!userModel[0].getPhoto().equals("noPhoto"))
                    Glide.with(Application.appContext)
                            .load(userModel[0].getPhoto())
                            .error(R.drawable.ic_round_error_outline_24)
                            .thumbnail(0.5f)
                            .apply(options)
                            .into(holder.ivUserAvatar);
                else
                    holder.ivUserAvatar.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24);

                reference.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.tvTime.setText(GLOBAL.getTimeAgo(postModel.getTime())); //TODO:GET DIFFERENT BETWEEN TWO TIMES
        holder.tvHint.setText(postModel.getHint());

        if (postModel.getType().equals("video")) {
            holder.youTubePlayerView.setVisibility(View.VISIBLE);
            holder.ivPost.setVisibility(View.GONE);
            holder.cueVideo(postModel.getVideo());

        } else {
            holder.youTubePlayerView.setVisibility(View.GONE);
            holder.ivPost.setVisibility(View.VISIBLE);
            Glide.with(Application.appContext).load(postModel.getImage()).error(R.drawable.ic_round_error_outline_24).into(holder.ivPost);
        }
        holder.tvLike.setTag("No");
        final String[] avatar = new String[1];
        if (postModel.getCommentEnable() == 1) {
            holder.lnComment.setVisibility(View.VISIBLE);
            holder.tvViewComments.setVisibility(View.VISIBLE);
        } else {
            holder.lnComment.setVisibility(View.GONE);
            holder.tvViewComments.setVisibility(View.GONE);
        }
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postModel.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tvLike.setText(String.valueOf(snapshot.getChildrenCount()));
                if (snapshot.child(uid).exists()) {
                    holder.tvLike.setTag("Yes");
                    holder.ivLike.setImageResource(R.drawable.ic_baseline_favorite_24);
                } else {
                    holder.tvLike.setTag("No");
                    holder.ivLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postModel.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tvComment.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.lnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecycleViewItemClick(holder.ivLike, postModel, holder.tvLike.getTag() == "Yes");
            }
        });
        holder.lnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCommentClickListener(postModel, userModel[0]);
            }
        });
        holder.tvViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentClickListener(postModel, userModel[0]);
            }
        });
        holder.ivUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.iconAvatarOnClick(postModel.getUid());
            }
        });
        holder.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.share(postModel.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        YouTubePlayerView youTubePlayerView;
        YouTubePlayer youTubePlayer;
        String currentVideoId;
        TextView tvName;
        TextView tvTime;
        TextView tvHint;
        TextView tvLike;
        TextView tvViewComments;
        TextView tvComment;
        ImageView ivPost;
        ImageView ivShare;
        ImageView ivLike;
        ImageView ivUserAvatar;
        ImageView ivUserVerified;
        LinearLayout lnLike;
        LinearLayout lnComment;

        public ViewHolder(PostItemBinding binding) {
            super(binding.getRoot());
            ivShare = binding.ivSharePost;
            tvName = binding.tvUserNamePost;
            tvTime = binding.tvTimePost;
            tvHint = binding.tvHintPost;
            tvLike = binding.tvLikePost;
            tvViewComments = binding.tvViewComments;
            tvComment = binding.tvCommentPost;
            ivPost = binding.ivPost;
            ivUserVerified = binding.ivAccountVerifiedPost;
            ivLike = binding.ivLikePost;
            ivUserAvatar = binding.ivUserImagePost;
            lnLike = binding.lnLikePost;
            lnComment = binding.lnCommentPost;
            youTubePlayerView = binding.videoPost;
            lifecycle.addObserver(youTubePlayerView);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer;
                    if (currentVideoId != null)
                        youTubePlayer.cueVideo(currentVideoId, 0);
                }
            });
        }

        void cueVideo(String videoId) {
            currentVideoId = videoId;

            if (youTubePlayer == null)
                return;

            youTubePlayer.cueVideo(videoId, 0);
        }
    }

}