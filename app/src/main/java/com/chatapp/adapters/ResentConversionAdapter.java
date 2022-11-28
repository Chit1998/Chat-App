package com.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.R;
import com.chatapp.listeners.ConversionListener;
import com.chatapp.models.ChatMessageModel;
import com.chatapp.models.UserDataModel;

import java.util.List;

public class ResentConversionAdapter extends RecyclerView.Adapter<ResentConversionAdapter.ResentConversionVH> {

    private Context context;
    private final List<ChatMessageModel> list;
    private final ConversionListener conversionListener;

    public ResentConversionAdapter(Context context, List<ChatMessageModel> list, ConversionListener listener) {
        this.context = context;
        this.list = list;
        conversionListener = listener;
    }

    @NonNull
    @Override
    public ResentConversionAdapter.ResentConversionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResentConversionVH(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_resent_coversion,
                                parent,
                                false
                        )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ResentConversionAdapter.ResentConversionVH holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ResentConversionVH extends RecyclerView.ViewHolder {
        ImageView image_user_profile_rv;
        TextView text_username_rv, text_about_rv;
        public ResentConversionVH(@NonNull View itemView) {
            super(itemView);

            image_user_profile_rv = itemView.findViewById(R.id.image_user_profile_rv);
            text_username_rv = itemView.findViewById(R.id.text_username_rv);
            text_about_rv = itemView.findViewById(R.id.text_about_rv);
        }

        void setData(ChatMessageModel messageModel){
            Glide.with(context)
                    .load(messageModel.conversionImage)
                    .centerCrop()
                    .into(image_user_profile_rv);
            text_username_rv.setText(messageModel.conversionName);
            text_about_rv.setText(messageModel.messageId);

            itemView.setOnClickListener(v -> {
                UserDataModel dataModel = new UserDataModel();
                dataModel.uid = messageModel.conversionId;
                dataModel.name = messageModel.conversionName;
                dataModel.url = messageModel.conversionImage;
                conversionListener.onConversionClicked(dataModel);

            });
        }
    }
}
