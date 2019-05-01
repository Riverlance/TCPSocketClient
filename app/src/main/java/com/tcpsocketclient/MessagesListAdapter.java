package com.tcpsocketclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcpsocketclient.tcpsocketclient.R;

import java.util.ArrayList;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.MessageViewHolder> {
    private ArrayList<Message> messagesList;
    private OnItemClickListener onItemClickListener;

    public MessagesListAdapter(ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 1 ? R.layout.content_message_sent_row : R.layout.content_message_received_row, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder(view, onItemClickListener);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);
        holder.usernameTextView.setText(message.username);
        holder.valueTextView.setText(message.value);

        // Hidden values
        Bundle bundle = new Bundle();
        bundle.putBoolean("sentByMe", message.sentByMe);
        holder.valueTextView.setTag(bundle);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void setOnClickListener() {
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messagesList.get(position);
        return message.sentByMe ? 1 : 0;
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView valueTextView;

        public MessageViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
