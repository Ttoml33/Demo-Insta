package com.tomlee.demoinsta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tomlee.demoinsta.Fragments.PostDetailFragment;
import com.tomlee.demoinsta.Model.Posts;
import com.tomlee.demoinsta.Model.User;
import com.tomlee.demoinsta.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{
    private Context mContext;
    private List<Posts>mPosts;

    public PhotoAdapter(Context mContext, List<Posts> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.photo_item,parent,false);

     return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posts posts=mPosts.get(position);
        Picasso.get().load(posts.getImageUrl()).placeholder(R.drawable.ic_person_foreground).
                into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit().
                        putString("postID", posts.getPostID()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_Container,new PostDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mPosts!=null){
            return mPosts.size();
        }else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage=itemView.findViewById(R.id.postImage);
        }
    }
}
