package com.chatapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.R;
import com.chatapp.listeners.UsersListener;
import com.chatapp.models.UserDataModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> {
    private Context context;
    private final List<UserDataModel> list;
    private final UsersListener usersListener;


    public UserAdapter(Context context, List<UserDataModel> list, UsersListener listener) {
        this.context = context;
        this.list = list;
        this.usersListener = listener;
    }

    @NonNull
    @Override
    public UserAdapter.UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserVH(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.rv_users_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserVH holder, int position) {
        holder.setUserData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserVH extends RecyclerView.ViewHolder {
        ImageView image_user_profile_rv;
        TextView text_username_rv,text_about_rv;
        public UserVH(@NonNull View itemView) {
            super(itemView);
            image_user_profile_rv = itemView.findViewById(R.id.image_user_profile_rv);
            text_username_rv = itemView.findViewById(R.id.text_username_rv);
            text_about_rv = itemView.findViewById(R.id.text_about_rv);
        }

        void setUserData(UserDataModel userData){
            text_username_rv.setText(userData.getName());
            text_about_rv.setText(userData.getAbout());
            Glide
                    .with(context.getApplicationContext())
                    .load(userData.getUrl())
                    .centerCrop()
                    .into(image_user_profile_rv);

            itemView.setOnClickListener(v -> usersListener.onUserClicked(userData));
        }
    }
}
