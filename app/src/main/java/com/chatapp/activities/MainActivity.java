package com.chatapp.activities;


import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chatapp.R;
import com.chatapp.adapters.ResentConversionAdapter;
import com.chatapp.help.Preferences;
import com.chatapp.listeners.ConversionListener;
import com.chatapp.models.ChatMessageModel;
import com.chatapp.models.UserDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener {

    private static final String TAG = "MainActivity";
    private Preferences preferences;
    private HashMap<String, String> userData;
    private FirebaseAuth auth;
    private RecyclerView rv_chat_list;
    private List<ChatMessageModel> conversion;
    private ResentConversionAdapter adapter;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        init();
        preferences = new Preferences(this);
        userData = preferences.getLogInDetails();
        getToken();
        listenConversion();
    }

    private void init(){
        rv_chat_list = findViewById(R.id.rv_chat_list);
        progressBar = findViewById(R.id.main_progressBar);
        conversion = new ArrayList<>();
        adapter = new ResentConversionAdapter(this, conversion, this);
        rv_chat_list.setAdapter(adapter);
        findViewById(R.id.text_logout)
                .setOnClickListener(v -> {
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("token", FieldValue.delete());
                    DocumentReference reference = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(auth.getUid());
                    reference.update(updates)
                            .addOnSuccessListener(unused -> {
                                preferences.dataClear();
                                auth.signOut();
                                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                                finish();
                            });
                });

        findViewById(R.id.fabAddUser)
                .setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),UsersActivity.class)));
    }

    @Override
    protected void onStart() {
        if (auth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            finish();
        }else {

        }
        super.onStart();
    }

    private void listenConversion(){
        firestore.collection("conversions")
                .whereEqualTo("senderId",auth.getUid())
                .addSnapshotListener(eventListener);

        firestore.collection("conversions")
                .whereEqualTo("receiverId", auth.getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null){
            return;
        }

        if (value != null){
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString("senderId");
                    String receiverId = documentChange.getDocument().getString("receiverId");
                    ChatMessageModel messageModel = new ChatMessageModel();
                    messageModel.senderId = senderId;
                    messageModel.receiverId = receiverId;

                    if (userData.get(Preferences.USER_ID).equals(senderId)){
                        messageModel.conversionName = documentChange.getDocument().getString("receiverName");
                        messageModel.conversionImage = documentChange.getDocument().getString("receiverImage");
                        messageModel.conversionId = documentChange.getDocument().getString("receiverId");
                    }else {
                        messageModel.conversionName = documentChange.getDocument().getString("senderName");
                        messageModel.conversionImage = documentChange.getDocument().getString("senderImage");
                        messageModel.conversionId = documentChange.getDocument().getString("senderId");
                    }
                    messageModel.messageId = documentChange.getDocument().getString("message");
                    messageModel.dateObject = documentChange.getDocument().getDate("timestamp");
                    conversion.add(messageModel);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i = 0; i < conversion.size(); i++){
                        String senderId = documentChange.getDocument().getString("senderId");
                        String receiverId = documentChange.getDocument().getString("receiverId");
                        if (conversion.get(i).senderId.equals(senderId) &&
                                        conversion.get(i).receiverId.equals(receiverId)){
                            conversion.get(i).messageId = documentChange.getDocument().getString("message");
                            conversion.get(i).dateObject = documentChange.getDocument().getDate("timestamp");
                            break;
                        }
                    }
                }
            }

            Collections.sort(conversion, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            adapter.notifyDataSetChanged();
            rv_chat_list.smoothScrollToPosition(0);
            progressBar.setVisibility(View.INVISIBLE);
        }
    });

//    TODO Token firebase messaging generate
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        preferences.setToken(token);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("users")
                .document(auth.getUid());
        reference.update("token",token)
                .addOnSuccessListener(unused -> Log.d(TAG, "updateToken: "+token))
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onConversionClicked(UserDataModel userDataModel) {
        Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
        intent.putExtra("user", userDataModel);
        startActivity(intent);
    }
}