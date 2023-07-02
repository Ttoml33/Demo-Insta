package com.tomlee.demoinsta.Fragments;

import android.app.SearchManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tomlee.demoinsta.Adapter.UserAdapter;
import com.tomlee.demoinsta.Model.User;
import com.tomlee.demoinsta.R;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private RecyclerView RCusers;
    private ArrayList<User>mUsers;
    private UserAdapter userAdapter;
    private SearchView searchView;
       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
           View view=inflater.inflate(R.layout.fragment_search, container, false);

           mUsers=new ArrayList<>();
           searchView=view.findViewById(R.id.searchView);
           RCusers=view.findViewById(R.id.RCusers);
           userAdapter=new UserAdapter(getContext(),mUsers,true);

           RCusers.setHasFixedSize(true);
           RCusers.setLayoutManager(new LinearLayoutManager(getContext()));
           RCusers.setAdapter(userAdapter);
           readUsers();

           searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
               @Override
               public boolean onQueryTextSubmit(String s) {
                   searchUsers(s);
                   return false;
               }

               @Override
               public boolean onQueryTextChange(String s) {
                   searchUsers(s);
                   return false;
               }
           });

        return view;
    }

    private void readUsers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user =dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Users").orderByChild("UserName")
                .startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }

                if (mUsers.isEmpty()) {
                    Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
                }

                // Notify the adapter of data changes to update the UI
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

}