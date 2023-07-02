package com.tomlee.demoinsta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tomlee.demoinsta.Fragments.PostDetailFragment;
import com.tomlee.demoinsta.Fragments.ProfileFragment;
import com.tomlee.demoinsta.Model.Notification;
import com.tomlee.demoinsta.Model.Posts;
import com.tomlee.demoinsta.Model.User;
import com.tomlee.demoinsta.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private List<Notification>mNotifications;
    private Context mContext;

    public NotificationAdapter(List<Notification> mNotifications, Context mContext) {
        this.mNotifications = mNotifications;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification=mNotifications.get(position);

        getUser(holder.imageProfile,holder.txtUserName,notification.getUserID());
        holder.txtComment.setText(notification.getText());
        
        if (notification.isPost()){
            holder.postImg.setVisibility(View.VISIBLE);
                getPostImg(holder.postImg,notification.getPostID());
        }else {
            holder.postImg.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isPost()){
                    mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit().
                            putString("postID", notification.getPostID()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().
                            beginTransaction().replace(R.id.fragment_Container,new PostDetailFragment()).commit();
                }else {
                    mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().
                            putString("profileID",notification.getUserID()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().
                            beginTransaction().replace(R.id.fragment_Container,new ProfileFragment()).commit();
                }
            }
        });
    }

    private void getPostImg(ImageView postImg, String postID) {
        FirebaseDatabase.getInstance().getReference().child("POSTS").child(postID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Posts posts=snapshot.getValue(Posts.class);

                Picasso.get().load(posts.getImageUrl()).placeholder(R.drawable.icon).into(postImg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(CircleImageView imageProfile, TextView txtUserName, String userID) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")){
                    imageProfile.setImageResource(R.drawable.ic_person_foreground);
                }else {
                    Picasso.get().load(user.getImageUrl()).into(imageProfile);
                }
                txtUserName.setText(user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mNotifications!=null){
           return mNotifications.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView imageProfile;
        private TextView txtUserName,txtComment;
        private ImageView postImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImg=itemView.findViewById(R.id.postImg);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            txtUserName=itemView.findViewById(R.id.txtUserName);
            txtComment=itemView.findViewById(R.id.txtComment);

        }
    }
}
