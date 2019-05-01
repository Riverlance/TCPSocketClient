package com.tcpsocketclient;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcpsocketclient.tcpsocketclient.R;

import java.util.ArrayList;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {
    private ArrayList<User> usersList;
    private OnItemClickListener onItemClickListener;

    public UsersListAdapter(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_chat_list_row, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view, onItemClickListener);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.titleTextView.setText(user.username);
        boolean isOnline = MainActivity.mainActivity.usersMap.containsKey(user.username);
        holder.subtitleTextView.setText(isOnline ? "Online" : "Offline");
        holder.subtitleTextView.setTextColor(MainActivity.mainActivity.getResources().getColor(isOnline ? R.color.colorOnlineStatus : R.color.colorOfflineStatus));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView subtitleTextView;

        public UserViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            subtitleTextView = itemView.findViewById(R.id.subtitleTextView);

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
