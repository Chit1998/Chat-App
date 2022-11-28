package com.chatapp.activities;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chatapp.R;
import com.chatapp.adapters.ChatAdapter;
import com.chatapp.help.Constant;
import com.chatapp.help.Preferences;
import com.chatapp.models.ChatMessageModel;
import com.chatapp.models.UserDataModel;
import com.chatapp.network.ApiClient;
import com.chatapp.network.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsActivity extends BaseActivity {

    private UserDataModel dataModel;
    private TextView text_username;
    private List<ChatMessageModel> list;
    private ChatAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private RecyclerView rv_chat_messages;
    private EditText eInputUserMessage;
    private ProgressBar progressBar;
    private String conversionId = null;
    private Preferences preference;
    private HashMap<String, String> userData;
    private Boolean isReceiverAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        init();
        setListener();
        loadReceiverDetails();
        preference = new Preferences(this);
        userData = preference.getLogInDetails();


        adapter = new ChatAdapter(list,auth.getUid());
        rv_chat_messages.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        listenMessage();

    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put("senderId", auth.getUid());
        message.put("receiverId", dataModel.uid);
//        message.put("receiverImage", dataModel.url);
        message.put("message",eInputUserMessage.getText().toString());
        message.put("timestamp",new Date());
        firestore.collection("chats").add(message);
        if (conversionId != null){
            UpdateConversion(eInputUserMessage.getText().toString());
        }else {
            HashMap<String, Object> conversionMap = new HashMap<>();
            conversionMap.put("senderId",auth.getUid());
            conversionMap.put("senderName",userData.get(Preferences.KEY_PROFILE_NAME));
            conversionMap.put("senderImage",userData.get(Preferences.PROFILE_IMAGE));
            conversionMap.put("receiverId",dataModel.uid);
            conversionMap.put("receiverName",dataModel.name);
            conversionMap.put("receiverImage",dataModel.url);
            conversionMap.put("message",eInputUserMessage.getText().toString());
            conversionMap.put("timestamp",new Date());
            addConversion(conversionMap);
        }

        if (!isReceiverAvailable){
            try {
                JSONArray jsonTokens = new JSONArray();
                jsonTokens.put(dataModel.token);

                JSONObject data = new JSONObject();
                data.put("uid",userData.get(Preferences.USER_ID));
                data.put("name",userData.get(Preferences.KEY_PROFILE_NAME));
                data.put("token",userData.get(Preferences.USER_TOKEN));
                data.put("message",eInputUserMessage.getText().toString());
                JSONObject body = new JSONObject();
                body.put(Constant.REMOTE_MSG_DATA, data);
                body.put(Constant.REMOTE_MSG_REGISTRATION_IDS, jsonTokens);

                sendNotification(body.toString());
            }catch (Exception e){
                e.getMessage();
                Constant.setMessage(ChatsActivity.this, e.getMessage());
            }
        }
        eInputUserMessage.setText("");
    }

    private void sendNotification(String messageBody){
        ApiClient.getClient()
                .create(ApiService.class)
                .sendMessage(Constant.getRemoteMsgHeaders(),
                        messageBody
                ).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()){
                            try {
                                if (response.body() != null){
                                    JSONObject responseJson = new JSONObject(response.body());
                                    JSONArray results = responseJson.getJSONArray("results");
                                    if (responseJson.getInt("failure") == 1){
                                        JSONObject error = (JSONObject) results.get(0);
                                        Constant.setMessage(ChatsActivity.this,error.getString("error"));
                                        return;
                                    }
                                }
                            }catch (JSONException e){
                                e.getMessage();
                            }
                            Constant.setMessage(ChatsActivity.this, "Notification sent successfully");
                        }else {
                            Constant.setMessage(ChatsActivity.this, "Error "+response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Constant.setMessage(ChatsActivity.this,t.getMessage());
                    }
                });
    }

    private void listenAvailabilityOfReceiver(){
        firestore.collection("users")
                .document(dataModel.uid)
                .addSnapshotListener(ChatsActivity.this, ((value, error) -> {
                    if (error != null){
                        return;
                    }
                    if (value != null){
                        if (value.getLong(Constant.KEY_AVAILABILITY) != null){
                            int availability = Objects.requireNonNull(value.getLong(Constant.KEY_AVAILABILITY)).intValue();
                            isReceiverAvailable = availability == 1;
                        }
                        dataModel.token = value.getString("token");
                        if (dataModel.url == null){
                            dataModel.url = value.getString("image");
                            adapter.setReceiverProfileImage(dataModel.url);
                            adapter.notifyItemRangeChanged(0, list.size());
                        }
                    }
                    if (isReceiverAvailable){
                        findViewById(R.id.text_user_availability).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.text_user_availability).setVisibility(View.GONE);
                    }
                }));
    }

    private void listenMessage(){
        firestore.collection("chats")
                .whereEqualTo("senderId",auth.getUid())
                .whereEqualTo("receiverId",dataModel.uid)
                .addSnapshotListener(eventListener);
        firestore.collection("chats")
                .whereEqualTo("senderId",dataModel.uid)
                .whereEqualTo("receiverId", auth.getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null){
            return;
        }
        if (value != null){
            int count = list.size();
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessageModel model = new ChatMessageModel();
                    model.senderId = documentChange.getDocument().getString("senderId");
                    model.receiverId = documentChange.getDocument().getString("receiverId");
                    model.messageId = documentChange.getDocument().getString("message");
                    model.dateObject = documentChange.getDocument().getDate("timestamp");
                    model.dateTime = getReadableDateTime(documentChange.getDocument().getDate("timestamp"));
                    list.add(model);
                }
            }
            Collections.sort(list,(obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0){
                adapter.notifyDataSetChanged();
            }else {
                adapter.notifyItemRangeInserted(list.size(),list.size());
                rv_chat_messages.smoothScrollToPosition(list.size() - 1);
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
        if (conversionId == null){
            checkForConversion();
        }
    });

    private void init(){
        text_username = findViewById(R.id.text_username);
        rv_chat_messages = findViewById(R.id.rv_chat_messages);
        eInputUserMessage = findViewById(R.id.eInputUserMessage);
        progressBar = findViewById(R.id.chatProgressBar);
    }

    private void loadReceiverDetails(){
        dataModel = (UserDataModel) getIntent().getSerializableExtra("user");
        text_username.setText(dataModel.name);
    }

    private void setListener(){
        findViewById(R.id.image_chat_app_back)
                .setOnClickListener(v -> onBackPressed());
        findViewById(R.id.layoutSend)
                .setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion){
        firestore.collection("conversions")
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    private void UpdateConversion(String message){
        DocumentReference documentReference =
                firestore.collection("conversions")
                        .document(conversionId);
        documentReference.update(
                "message", message,
                "timestamp", new Date()
        );
    }

    private void checkForConversion(){
        if (list.size() != 0){
            checkConversionRemotely(auth.getUid(), dataModel.uid);
        }
        checkConversionRemotely(
                dataModel.uid,
                auth.getUid()
        );
    }

    private void checkConversionRemotely(String senderId, String receiverId){
        firestore.collection("conversions")
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }

}