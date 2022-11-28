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
import com.chatapp.help.Constant;
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
import java.util.Objects;

public class MainActivity extends BaseActivity implements ConversionListener {

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
                    updates.put(Constant.USER_TOKEN, FieldValue.delete());
                    DocumentReference reference = FirebaseFirestore.getInstance()
                            .collection(Constant.USER_DIR)
                            .document(Objects.requireNonNull(auth.getUid()));
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
        firestore.collection(Constant.KEY_CONVERSIONS)
                .whereEqualTo(Constant.KEY_SENDER_ID,auth.getUid())
                .addSnapshotListener(eventListener);

        firestore.collection(Constant.KEY_CONVERSIONS)
                .whereEqualTo(Constant.KEY_RECEIVER_ID, auth.getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null){
            return;
        }

        if (value != null){
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                    ChatMessageModel messageModel = new ChatMessageModel();
                    messageModel.senderId = senderId;
                    messageModel.receiverId = receiverId;

                    if (userData.get(Constant.USER_ID).equals(senderId)){
                        messageModel.conversionName = documentChange.getDocument().getString(Constant.KEY_RECEIVER_NAME);
                        messageModel.conversionImage = documentChange.getDocument().getString(Constant.KEY_RECEIVER_IMAGE_URL);
                        messageModel.conversionId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                    }else {
                        messageModel.conversionName = documentChange.getDocument().getString(Constant.KEY_SENDER_NAME);
                        messageModel.conversionImage = documentChange.getDocument().getString(Constant.KEY_SENDER_IMAGE_URL);
                        messageModel.conversionId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                    }
                    messageModel.messageId = documentChange.getDocument().getString(Constant.KEY_MESSAGE);
                    messageModel.dateObject = documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
                    conversion.add(messageModel);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i = 0; i < conversion.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constant.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constant.KEY_RECEIVER_ID);
                        if (conversion.get(i).senderId.equals(senderId) &&
                                        conversion.get(i).receiverId.equals(receiverId)){
                            conversion.get(i).messageId = documentChange.getDocument().getString(Constant.KEY_MESSAGE);
                            conversion.get(i).dateObject = documentChange.getDocument().getDate(Constant.KEY_TIMESTAMP);
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
        DocumentReference reference = db.collection(Constant.USER_DIR)
                .document(auth.getUid());
        reference.update(Constant.USER_TOKEN,token)
                .addOnSuccessListener(unused -> Log.d(Constant.TAG_MAIN, "updateToken: "+token))
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onConversionClicked(UserDataModel userDataModel) {
        Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
        intent.putExtra(Constant.USER, userDataModel);
        startActivity(intent);
    }
}