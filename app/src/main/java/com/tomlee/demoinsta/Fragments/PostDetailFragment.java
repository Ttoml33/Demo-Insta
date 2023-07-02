package com.tomlee.demoinsta.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tomlee.demoinsta.Adapter.PostAdapter;
import com.tomlee.demoinsta.Model.Posts;
import com.tomlee.demoinsta.R;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Posts>postsList;
    private String postID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

       postID=getContext().getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("postID","none");

       recyclerView=view.findViewById(R.id.recyclerView);
       recyclerView.hasFixedSize();
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       postsList=new ArrayList<>();
       postAdapter=new PostAdapter(getContext(),postsList);
       recyclerView.setAdapter(postAdapter);

        FirebaseDatabase.getInstance().getReference().child("POSTS").child(postID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                postsList.add(snapshot.getValue(Posts.class));

                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}