package com.example.whatsappchatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappchatapp.Models.Message;
import com.example.whatsappchatapp.R;
import com.example.whatsappchatapp.databinding.ItemReceiveBinding;
import com.example.whatsappchatapp.databinding.ItemSentBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    public MessagesAdapter(Context context , ArrayList<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    @NonNull

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent , parent , false);
            return new sentViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive , parent , false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return  ITEM_SENT;
        }
        else{
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(holder.getClass()==(sentViewHolder.class)){
            sentViewHolder viewHolder = (sentViewHolder) holder;

            if(message.getMessage().equals("photo")){
                viewHolder.binding.chatImage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);

                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.chatImage);
            }
            viewHolder.binding.message.setText(message.getMessage());
        }
        else{

            reciverViewHolder viewHolder = (reciverViewHolder) holder;
            if(message.getMessage().equals("photo")){
                viewHolder.binding.chatImage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);

                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.chatImage);
            }
            viewHolder.binding.message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class sentViewHolder extends RecyclerView.ViewHolder{
            ItemSentBinding binding;
        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class reciverViewHolder extends RecyclerView.ViewHolder{
        ItemReceiveBinding binding ;
        public reciverViewHolder(@NonNull  View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
