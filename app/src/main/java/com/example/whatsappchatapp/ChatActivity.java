package com.example.whatsappchatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappchatapp.Adapters.MessagesAdapter;
import com.example.whatsappchatapp.Models.Message;
import com.example.whatsappchatapp.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messages;
    FirebaseDatabase database;
    FirebaseStorage storage;

    String senderUId;
    String receiverUId;

    String senderRoom , reciverRoom;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        storage =FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image");
        dialog.setCancelable(false);
        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this , messages);
        binding.messsagerecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.messsagerecyclerView.setAdapter(messagesAdapter);

        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("name");
         receiverUId = getIntent().getStringExtra("uid");
        String profileImage = getIntent().getStringExtra("profileImage");
         senderUId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        senderRoom = senderUId +receiverUId;
        reciverRoom = receiverUId +senderUId;
        binding.activename.setText(name);
        Glide.with(this).load(profileImage).into(binding.profiledp);

        database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            messages.clear();
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                Message message = snapshot1.getValue(Message.class);
                                messages.add(message);
                            }
                            messagesAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , 25);

            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();
                Date date = new Date();
                Message message = new Message(messageTxt , senderUId , date.getTime());
                binding.messageBox.setText("");
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats")
                                .child(reciverRoom)
                                .child("messages")
                                .push()
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==25){
            if(data!=null){
                if(data.getData()!=null){
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull  Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                     String filePath = uri.toString();

                                        String messageTxt = binding.messageBox.getText().toString();
                                        Date date = new Date();
                                        Message message = new Message(messageTxt , senderUId , date.getTime());
                                        message.setImageUrl(filePath);
                                        message.setMessage("photo");
                                        binding.messageBox.setText("");
                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .push()
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference().child("chats")
                                                        .child(reciverRoom)
                                                        .child("messages")
                                                        .push()
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });

                                            }
                                        });

                                        Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                }
            }
        }
    }
}