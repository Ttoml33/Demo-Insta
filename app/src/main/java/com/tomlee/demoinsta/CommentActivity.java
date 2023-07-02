package com.tomlee.demoinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tomlee.demoinsta.Adapter.CommentAdapter;
import com.tomlee.demoinsta.Model.Comment;
import com.tomlee.demoinsta.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView RCpostComments;
    private CommentAdapter commentAdapter;
    private List<Comment>commentList;
    private ImageView photoProfile;
    private TextView post;
    private EditText addComment;
    private String postID,authID;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar=findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        authID=intent.getStringExtra("authID");
        postID=intent.getStringExtra("postID");

        RCpostComments=findViewById(R.id.RCpostComments);
        RCpostComments.setHasFixedSize(true);
        RCpostComments.setLayoutManager(new LinearLayoutManager(this));
        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentList,postID);
        RCpostComments.setAdapter(commentAdapter);

        photoProfile=findViewById(R.id.photoProfile);
        post=findViewById(R.id.post);
        addComment=findViewById(R.id.addComment);

        fUser= FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();
        readComments();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(CommentActivity.this, "No Comment Added", Toast.LENGTH_SHORT).show();
                }else {
                    putComment();
                }
            }
        });
    }

    private void readComments() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Comment comments=dataSnapshot.getValue(Comment.class);
                    commentList.add(comments);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void putComment() {
        HashMap<String,Object>map=new HashMap<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().
                child("Comments").child(postID);
        String id=reference.push().getKey();
        map.put("id", id);
        map.put("comment" ,addComment.getText().toString());
        map.put("publisher", fUser.getUid());

        reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            addComment.setText("");
                            Toast.makeText(CommentActivity.this, "Comment has been Added", Toast.LENGTH_SHORT).show();
                            getNotification(fUser.getUid());

                        }else {
                            Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void getNotification(String id) {
        HashMap<String,Object>map=new HashMap<>();
        map.put("userID",id);
        map.put("text"," Commented on your Post ");
        map.put("postID","");
        map.put("isPost",false);

        FirebaseDatabase.getInstance().getReference().child("Notification").
                child(fUser.getUid()).push().setValue(map);
    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")){
                    photoProfile.setImageResource(R.drawable.ic_person_foreground);
                }else {
                    Picasso.get().load(user.getImageUrl()).into(photoProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}