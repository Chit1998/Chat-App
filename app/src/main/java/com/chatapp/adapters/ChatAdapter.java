package com.chatapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.R;
import com.chatapp.models.ChatMessageModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatVH> {

    private final List<ChatMessageModel> list;
    private final String sendId;
    private String urlImage;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVER = 2;

    public void setReceiverProfileImage(String url) {
        urlImage = url;
    }

    public ChatAdapter(List<ChatMessageModel> list, String sendId) {
        this.list = list;
        this.sendId = sendId;
    }
    
    @NonNull
    @Override
    public ChatAdapter.ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new ChatVH(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_sent_message,parent,false)
            );
        }else {
            return new ChatVH(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_receiver_message,parent,false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatVH holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            holder.setData(list.get(position));
        }else {
            holder.setData(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).senderId.equals(sendId)){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    class ChatVH extends RecyclerView.ViewHolder {
        final TextView text_message;
        final TextView textDateTime;

        public ChatVH(@NonNull View itemView) {
            super(itemView);
            text_message = itemView.findViewById(R.id.text_message);
            textDateTime = itemView.findViewById(R.id.textDateTime);
        }

        void setData(ChatMessageModel model){
            text_message.setText(model.messageId);
            textDateTime.setText(model.dateTime);
            if (urlImage != null){
                Toast.makeText(itemView.getContext(), urlImage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
