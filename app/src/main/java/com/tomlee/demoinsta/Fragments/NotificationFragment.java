package com.tomlee.demoinsta.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tomlee.demoinsta.Adapter.NotificationAdapter;
import com.tomlee.demoinsta.Model.Notification;
import com.tomlee.demoinsta.Model.User;
import com.tomlee.demoinsta.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationFragment extends Fragment {
    private List<Notification>notificationList;
    private NotificationAdapter notificationAdapter;
    private RecyclerView RcNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notification, container, false);

        RcNotifications=view.findViewById(R.id.RcNotifications);
        RcNotifications.setHasFixedSize(true);
        RcNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(notificationList,getContext());
        RcNotifications.setAdapter(notificationAdapter);

        readNotification();
        return view;
    }

    private void readNotification() {
        FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            notificationList.add(dataSnapshot.getValue(Notification.class));
                        }
                        Collections.reverse(notificationList);
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}