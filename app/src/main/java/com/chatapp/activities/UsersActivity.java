package com.chatapp.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chatapp.R;
import com.chatapp.adapters.UserAdapter;
import com.chatapp.help.Constant;
import com.chatapp.listeners.UsersListener;
import com.chatapp.models.UserDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UsersListener{
    private FirebaseAuth auth;
    private RecyclerView rv_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        auth = FirebaseAuth.getInstance();

        rv_user = findViewById(R.id.rv_user_list);
        rv_user.setLayoutManager(new LinearLayoutManager(this));
        getUser();
    }

    private void getUser(){
        loading(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Constant.USER_DIR)
                        .get()
                        .addOnCompleteListener(task -> {
                            loading(false);
                            if (task.isSuccessful() && task.getResult() != null){
                                List<UserDataModel> models = new ArrayList<>();
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    if (auth.getUid().equals(snapshot.getId())){
                                        continue;
                                    }
                                    UserDataModel model = new UserDataModel();
                                    model.name = snapshot.getString("name");
                                    model.about = snapshot.getString("about");
                                    model.url = snapshot.getString("url");
                                    model.token = snapshot.getString("token");
                                    model.uid = snapshot.getString("uid");
                                    models.add(model);
                                }
                                if (models.size() > 0){
                                    Log.d(Constant.TAG_USERS, models.size()+"");
                                    UserAdapter adapter = new UserAdapter(UsersActivity.this, models, this);
                                    rv_user.setAdapter(adapter);
                                }else {
                                    showErrorMessage();
                                }
                            }else {
                                showErrorMessage();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Constant.setMessage(UsersActivity.this,e.getMessage());
                            Log.d(Constant.TAG_USERS, e.getMessage());
                        });
    }

    private void showErrorMessage(){
        Toast.makeText(this, "No user available", Toast.LENGTH_SHORT).show();
    }

    private void loading(boolean isLoading){
        if (isLoading){
            findViewById(R.id.user_progressBar).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.user_progressBar).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(UserDataModel user) {
        Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();


    }
}