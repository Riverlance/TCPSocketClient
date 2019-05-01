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

    public UsersListAdapter(ArrayList<User> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_chat_list_row, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view);
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

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            subtitleTextView = itemView.findViewById(R.id.subtitleTextView);
        }
    }
}
