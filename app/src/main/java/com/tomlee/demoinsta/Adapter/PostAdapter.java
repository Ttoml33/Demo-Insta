package com.tomlee.demoinsta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tomlee.demoinsta.CommentActivity;
import com.tomlee.demoinsta.FollowersActivity;
import com.tomlee.demoinsta.Fragments.PostDetailFragment;
import com.tomlee.demoinsta.Fragments.ProfileFragment;
import com.tomlee.demoinsta.Model.Posts;
import com.tomlee.demoinsta.Model.User;
import com.tomlee.demoinsta.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private Context mContext;
    private List<Posts>mPosts;

    private FirebaseUser firebaseUser;


    public PostAdapter(Context mContext, List<Posts> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Posts posts= mPosts.get(position);

        Picasso.get().load(posts.getImageUrl()).into(holder.postImage);
        holder.tvtDescription.setText(posts.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").
                child(posts.getPublisher()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        if (user.getImageUrl().equals("default")){
                            holder.profileImg.setImageResource(R.drawable.ic_person_foreground);
                        }else {
                            Picasso.get().load(user.getImageUrl()).into(holder.profileImg);
                        }
                        holder.txtUsername.setText(user.getUserName());
//                        holder.txtAuthor.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        isLiked(posts.getPostID(), holder.likeBtn);
        likesNumbers(posts.getPostID(), holder.txtLikes);
        CommentsNumber(posts.getPostID(),holder.txtNoOfComment);
        isSaved(posts.getPostID(),holder.savesBtn);

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checking if image has been liked then adding like if not
                if (holder.likeBtn.getTag().equals("Like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").
                            child(posts.getPostID()).child(firebaseUser.getUid()).setValue(true);
                    addNotification(posts.getPostID(),posts.getPublisher());
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").
                            child(posts.getPostID()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.commentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID",posts.getPostID());
                intent.putExtra("authID",posts.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.txtNoOfComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, CommentActivity.class);
                intent.putExtra("postID",posts.getPostID());
                intent.putExtra("authID",posts.getPublisher());
                mContext.startActivity(intent);
            }
        });
        holder.savesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.savesBtn.getTag().equals("Save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").
                            child(firebaseUser.getUid()).child(posts.getPostID()).setValue(true);

                    Toast.makeText(mContext, "Post is Saved", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").
                            child(firebaseUser.getUid()).child(posts.getPostID()).removeValue();

                            Toast.makeText(mContext, "Post Unsaved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().
                        putString("profileID", posts.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_Container,new ProfileFragment()).commit();
            }
        });
        holder.txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().
                        putString("profileID", posts.getPublisher()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_Container,new ProfileFragment()).commit();
            }
        });
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit().
                        putString("postID", posts.getPostID()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_Container,new PostDetailFragment()).commit();
            }
        });
        holder.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, FollowersActivity.class);
//                intent.putExtra("ID",firebaseUser.getUid());
                intent.putExtra("ID",posts.getPublisher());
                intent.putExtra("Likes","Likes");
                mContext.startActivity(intent);
            }
        });

    }

    private void addNotification(String postID, String publisher) {
        HashMap<String,Object>map=new HashMap<>();
        map.put("userID",publisher);
        map.put("text"," Liked your Post ");
        map.put("postID",postID);
        map.put("isPost",true);

        FirebaseDatabase.getInstance().getReference().child("Notification").
                child(firebaseUser.getUid()).push().setValue(map);
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
        public CircleImageView profileImg;
        public ImageView more,postImage,likeBtn,commentsBtn,savesBtn;
        public TextView txtUsername,txtAuthor,tvtDescription,txtNoOfComment,txtLikes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
// Initialize your views here
            profileImg = itemView.findViewById(R.id.profileImg);
            more = itemView.findViewById(R.id.more);
            postImage = itemView.findViewById(R.id.postImage);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentsBtn = itemView.findViewById(R.id.commentsBtn);
            savesBtn = itemView.findViewById(R.id.savesBtn);
            txtUsername = itemView.findViewById(R.id.txtUsername);
//            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            tvtDescription = itemView.findViewById(R.id.tvtDescription);
            txtNoOfComment = itemView.findViewById(R.id.txtNoOfComment);
            txtLikes=itemView.findViewById(R.id.txtLikes);
        }
    }

    private void isSaved(String postID, ImageView savesBtn) {
        FirebaseDatabase.getInstance().getReference().child("Saves").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postID).exists()){
                            savesBtn.setImageResource(R.drawable.ic_saved_foreground);
//                            Toast.makeText(mContext, "Photo Saved", Toast.LENGTH_SHORT).show();
                            savesBtn.setTag("Saved");

                        }else {
                            savesBtn.setImageResource(R.drawable.ic_save_foreground);
                            savesBtn.setTag("Save");
//                            Toast.makeText(mContext, "Photo not saved retry", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void isLiked(String postId,ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("Likes").
                child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(firebaseUser.getUid()).exists()){
                            imageView.setImageResource(R.drawable.ic_liked_foreground);
                            imageView.setTag("Liked");
                        }else {
                            imageView.setImageResource(R.drawable.ic_like_foreground);
                            imageView.setTag("Like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    private void CommentsNumber(String postId,TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Comments").
                child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        textView.setText("View all "+ snapshot.getChildrenCount()+" Comments");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void likesNumbers(String postId,TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Likes").
                child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        textView.setText(snapshot.getChildrenCount() +" Likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
