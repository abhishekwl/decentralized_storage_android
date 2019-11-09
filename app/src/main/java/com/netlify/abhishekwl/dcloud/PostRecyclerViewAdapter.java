package com.netlify.abhishekwl.dcloud;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    public String ipfsGetUrl;
    public ArrayList<Post> postArrayList;

    public PostRecyclerViewAdapter(Context context, ArrayList<Post> postArrayList) {
        ipfsGetUrl = context.getString(R.string.ipfs_fetch_url);
        ipfsGetUrl+="/ipfs/";
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostRecyclerViewAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerViewAdapter.PostViewHolder holder, int position) {
        holder.bind(postArrayList.get(position), holder.itemView);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        TextView authorNameTextView, contentTextView;
        ImageView authorImageView, contentImageView;
        VideoView videoView;
        MaterialButton shareButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            authorNameTextView = itemView.findViewById(R.id.postListItemAuthorNameTextView);
            contentImageView = itemView.findViewById(R.id.postListItemImageView);
            authorImageView = itemView.findViewById(R.id.postListItemAuthorImageView);
            contentTextView = itemView.findViewById(R.id.postListItemContentTextView);
            videoView = itemView.findViewById(R.id.postListItemVideoView);
            shareButton = itemView.findViewById(R.id.postListItemShareButton);
        }

        void bind(Post post, View itemView) {
            if (post.getAuthorName().contains("test")) post.setAuthorName("Abhishek");
            String imageUrl = ipfsGetUrl+post.getHash();
            Glide.with(itemView.getContext()).load("https://cdn.imgbin.com/3/12/17/imgbin-computer-icons-avatar-user-login-avatar-man-wearing-blue-shirt-illustration-mJrXLG07YnZUc2bH5pGfFKUhX.jpg").into(authorImageView);
            Glide.with(itemView.getContext()).load(imageUrl).into(contentImageView);
            authorNameTextView.setText(post.getAuthorName());
            contentTextView.setText(post.getDescription());
            if (post.getName().endsWith("mp4") || post.getName().endsWith("mp3") || post.getName().endsWith("wav")) {
                videoView.setVideoURI(Uri.parse(imageUrl));
                videoView.requestFocus();
                videoView.setVisibility(View.VISIBLE);
                contentImageView.setVisibility(View.GONE);
                videoView.setOnPreparedListener(mp -> videoView.start());
            }
            itemView.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ipfsGetUrl+post.getHash()));
                itemView.getContext().startActivity(browserIntent);
            });
            shareButton.setOnClickListener(v -> {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, post.getName());
                share.putExtra(Intent.EXTRA_TEXT, post.getAuthorName()+" : "+post.getDescription() +"\n\n"+imageUrl);
                itemView.getContext().startActivity(Intent.createChooser(share, "Share link!"));
            });
        }
    }
}
