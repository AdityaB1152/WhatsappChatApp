package com.example.whatsappchatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappchatapp.ChatActivity;
import com.example.whatsappchatapp.Models.User;
import com.example.whatsappchatapp.R;
import com.example.whatsappchatapp.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    Context context;

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    ArrayList<User> users;
    @NonNull

    @Override
    public UserViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation , parent , false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  UsersAdapter.UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.username.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                            .placeholder(R.drawable.avatar)
                            .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("name" , user.getName());
                intent.putExtra("uid" , user.getUid());
                intent.putExtra("profileImage" , user.getProfileImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        RowConversationBinding binding;

        public UserViewHolder(@NonNull  View itemView) {

            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
