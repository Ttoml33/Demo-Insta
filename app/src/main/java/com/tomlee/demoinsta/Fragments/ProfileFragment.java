package com.tomlee.demoinsta.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tomlee.demoinsta.Adapter.PhotoAdapter;
import com.tomlee.demoinsta.EditProfileActivity;
import com.tomlee.demoinsta.FollowersActivity;
import com.tomlee.demoinsta.Model.Posts;
import com.tomlee.demoinsta.Model.User;
import com.tomlee.demoinsta.OptionsActivity;
import com.tomlee.demoinsta.R;
import com.tomlee.demoinsta.StartActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private RecyclerView RCsavedPictures;
    private List<Posts> mySavedPostsList;
    private  PhotoAdapter savedPhotoAdapter;
    private RecyclerView RCviewPictures;
    private List<Posts>postsList;
    private PhotoAdapter photoAdapter;
    private CircleImageView imgProfile;
    private ImageView options;
    private ImageView myPictures,savedPictures;
    private TextView txtUserName,post,followers,following,fullName,bio;
    private FirebaseUser fUser;
     String profileID;
    private Button editProfile;


       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        String data=getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).
                getString("profileID","none");
        if (data.equals(null)){
            profileID=fUser.getUid();
        }else {
            profileID =data;
        }

           imgProfile=view.findViewById(R.id.imgProfile);
           options=view.findViewById(R.id.options);
           myPictures=view.findViewById(R.id.myPictures);
           savedPictures=view.findViewById(R.id.savedPictures);
           txtUserName=view.findViewById(R.id.txtUserName);
           post=view.findViewById(R.id.post);
           followers=view.findViewById(R.id.followers);
           following=view.findViewById(R.id.following);
           fullName=view.findViewById(R.id.fullName);
           bio=view.findViewById(R.id.bio);
           editProfile=view.findViewById(R.id.editProfile);

           RCviewPictures=view.findViewById(R.id.RCviewPictures);
           RCviewPictures.setHasFixedSize(true);
           RCviewPictures.setLayoutManager(new GridLayoutManager(getContext(),3));
           postsList=new ArrayList<>();
           photoAdapter=new PhotoAdapter(getContext(),postsList);
           RCviewPictures.setAdapter(photoAdapter);

           RCsavedPictures=view.findViewById(R.id.RCsavedPictures);
           RCsavedPictures.setHasFixedSize(true);
           RCsavedPictures.setLayoutManager(new GridLayoutManager(getContext(),3));
           mySavedPostsList=new ArrayList<>();
           savedPhotoAdapter=new PhotoAdapter(getContext(),mySavedPostsList);
           RCsavedPictures.setAdapter(savedPhotoAdapter);

           UserInfo();
           getFollowersAndFollowing();
           getPostCount();
           myPhotos();
           savedPhotos();

           if (profileID.equals(fUser.getUid())){
               editProfile.setText("Edit Profile");
           }else {
               checkFollowStatus();
           }
           editProfile.setOnClickListener(view1 -> {
            String btnText=editProfile.getText().toString();
            if (btnText.equals("Edit Profile")){
                startActivity(new Intent(getContext(),EditProfileActivity.class));
            }else {
                if (btnText.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child(fUser.getUid()).child("following").
                            child(profileID).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileID)
                            .child("followers").child(fUser.getUid())
                            .setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child(fUser.getUid()).child("following").
                            child(profileID).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(profileID)
                            .child("followers").child(fUser.getUid())
                            .removeValue();

                }
            }
           });
           RCviewPictures.setVisibility(View.VISIBLE);
           RCsavedPictures.setVisibility(View.GONE);

           myPictures.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   RCviewPictures.setVisibility(View.VISIBLE);
                   RCsavedPictures.setVisibility(View.GONE);
               }
           });
           savedPictures.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   RCviewPictures.setVisibility(View.GONE);
                   RCsavedPictures.setVisibility(View.VISIBLE);
               }
           });
           followers.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent=new Intent(getContext(), FollowersActivity.class);
                   intent.putExtra("ID",profileID);
                   intent.putExtra("tittle","followers");
                   startActivity(intent);
               }
           });
           following.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent=new Intent(getContext(), FollowersActivity.class);
                   intent.putExtra("ID",profileID);
                   intent.putExtra("tittle","following");
                   startActivity(intent);
               }
           });
           options.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   startActivity(new Intent(getContext(), OptionsActivity.class));
               }
           });

        return view;
    }

    private void savedPhotos() {
           List<String>savedIds=new ArrayList<>();
           FirebaseDatabase.getInstance().getReference().child("Saves").
                   child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                               savedIds.add(dataSnapshot.getKey());
                           }
                           FirebaseDatabase.getInstance().getReference().child("POSTS").
                                   addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                   mySavedPostsList.clear();
                                   for (DataSnapshot dSnapshot:snapshot1.getChildren()
                                        ) {
                                       Posts posts=dSnapshot.getValue(Posts.class);
                                       for (String id : savedIds){
                                           if (posts.getPostID().equals(id)){
                                               mySavedPostsList.add(posts);
                                           }
                                       }

                                   }
                                   savedPhotoAdapter.notifyDataSetChanged();
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
    }

    private void myPhotos() {
           FirebaseDatabase.getInstance().getReference().child("POSTS").
                   addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   postsList.clear();
                   for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                       Posts posts=dataSnapshot.getValue(Posts.class);
                       if (posts.getPublisher().equals(profileID)){
                           postsList.add(posts);
                       }
                   }
                   Collections.reverse(postsList);
                   photoAdapter.notifyDataSetChanged();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
    }
    private void checkFollowStatus() {
           FirebaseDatabase.getInstance().getReference().child("follow").
                   child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if (snapshot.child(profileID).exists()){
                               editProfile.setText("Following");
                           }else {
                               editProfile.setText("Follow");
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
    }

    private void getPostCount() {
           FirebaseDatabase.getInstance().getReference().child("POSTS").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   int counter=0;
                   for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                       Posts posts=dataSnapshot.getValue(Posts.class);
                       if (posts.getPublisher().equals(profileID)) counter++;
                   }
                   post.setText(String.valueOf(counter));
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
    }

    private void getFollowersAndFollowing() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().
                child("follow").child(profileID);
        reference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getImageUrl()).into(imgProfile);
                txtUserName.setText(user.getUserName());
                fullName.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}