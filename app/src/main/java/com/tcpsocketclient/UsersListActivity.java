package com.tcpsocketclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tcpsocketclient.tcpsocketclient.R;

public class UsersListActivity extends AppCompatActivity {
    // Constants
    public static final String DEFAULT_TOPBARTITLE = "Usuários";

    // Needed stuffs
    public RecyclerView recyclerView;
    public UsersListAdapter usersListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static Thread ticksThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        User user = MainActivity.mainActivity.getOnlineUser();
        if (user != null) {
            getSupportActionBar().setTitle(DEFAULT_TOPBARTITLE);
        }

        // Needed stuffs
        buildRecyclerView();

        // Needed stuffs
        // First onTick instantly
        onTick();

        // Needed stuffs
        // Executes periodically, once per 2 seconds
        ticksThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onTick();
                            }
                        });
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
        };
        ticksThread.start();


        Intent intent = getIntent();
        if (intent != null) {
            boolean requestUpdatedUsersList = intent.getBooleanExtra("requestUpdatedUsersList", false);
            intent.removeExtra("requestUpdatedUsersList");

            if (requestUpdatedUsersList) {
                // Request updated users list to add
                ProtocolSender protocolSender = new ProtocolSender();
                protocolSender.execute(String.format("%d", MainActivity.OPCODE_CTS_UPDATEDUSERSLIST));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        MainActivity.usersListActivity = this;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Thread should be interrupted since the parent does not exists
        if (ticksThread != null) {
            ticksThread.interrupt();
            ticksThread = null;
        }

        MainActivity.usersListActivity = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { // Request exit from MessageListActivity
            if (resultCode == 1) { // Exit from MessageListActivity
                finish();
            }
        }
    }

    private void onTick() {
        // Update online status of chats list elements
        for (int i = 0; i < MainActivity.mainActivity.chatsList.size(); i++) {
            boolean isOnline = MainActivity.mainActivity.usersMap.containsKey(MainActivity.mainActivity.chatsList.get(i).username);
            updateRow(i, isOnline);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        MainActivity.mainActivity.logout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chat_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logoutItem:
                MainActivity.mainActivity.logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateRow(int position, boolean isOnline) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder == null) {
            return;
        }

        View view = viewHolder.itemView;
        TextView subtitleTextView = view.findViewById(R.id.subtitleTextView);

        subtitleTextView.setText(isOnline ? "Online" : "Offline");
        subtitleTextView.setTextColor(MainActivity.mainActivity.getResources().getColor(isOnline ? R.color.colorOnlineStatus : R.color.colorOfflineStatus));
    }

    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.chatListRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        usersListAdapter = new UsersListAdapter(MainActivity.mainActivity.chatsList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(usersListAdapter);

        usersListAdapter.setOnItemClickListener(new UsersListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // If you change something in the selected data, use: usersListAdapter.notifyDataSetChanged();

                // Get online user based on saved username
                User user = MainActivity.mainActivity.getOnlineUser();
                if (user == null) {
                    return;
                }

                Intent intent = new Intent(UsersListActivity.this, MessageListActivity.class);
                String targetUsername = MainActivity.mainActivity.chatsList.get(position).username;

                /*
                if (targetUsername.equals(user.username)) {
                    Snackbar.make(recyclerView, "Você não pode conversar sozinho.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                */

                intent.putExtra("targetUsername", targetUsername);
                startActivityForResult(intent, 1); // With exit request code
            }
        });
    }
}
