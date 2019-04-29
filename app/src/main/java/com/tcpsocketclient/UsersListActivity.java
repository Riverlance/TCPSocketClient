package com.tcpsocketclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tcpsocketclient.tcpsocketclient.R;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter usersListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> usersList = new ArrayList<>();

    private static Thread ticksThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.usersListActivity = this;

        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        User user = MainActivity.mainActivity.loggedUser;
        if (user != null) {
            getSupportActionBar().setTitle(user.username);
        }

        // For TEST
        usersList.add(new User("User 1"));
        usersList.add(new User("User 2"));
        usersList.add(new User("User 3"));

        recyclerView = findViewById(R.id.chatListRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        usersListAdapter = new UsersListAdapter(usersList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(usersListAdapter);

        // First onTick instantly
        onTick();

        // Executes periodically, once per 5 seconds
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
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
        };
        ticksThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Thread should be interrupted since the parent does not exists
        ticksThread.interrupt();
        ticksThread = null;

        MainActivity.usersListActivity = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ProtocolSender protocolSender = new ProtocolSender();
        protocolSender.execute(String.format("%d", MainActivity.OPCODE_CTS_DISCONNECT));
    }

    private void onTick() {
        User user = MainActivity.mainActivity.loggedUser;
        if (user == null) {
            return;
        }

        ProtocolSender protocolSender = new ProtocolSender();
        protocolSender.execute(String.format("%d", MainActivity.OPCODE_CTS_USERSMAPSIGNAL));
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
                ProtocolSender protocolSender = new ProtocolSender();
                protocolSender.execute(String.format("%d", MainActivity.OPCODE_CTS_DISCONNECT));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();

        User user = new User((String) bundle.get("username"));
        //usersListAdapter.list.add(user);
        //usersListAdapter.notifyDataSetChanged();
    }



    public void addUser(User user, boolean notify) {
        usersList.add(user);
        if (notify) {
            usersListAdapter.notifyDataSetChanged();
        }
    }
    public void addUser(User user) {
        addUser(user, true);
    }

    public void removeUser(int i, boolean notify) {
        usersList.remove(i);
        if (notify) {
            usersListAdapter.notifyDataSetChanged();
        }
    }
    public void removeUser(int i) {
        removeUser(i, true);
    }
    public void removeUser(String username, boolean notify) {
        for (int i = 0; i < usersList.size(); i++) {
            User _user = usersList.get(i);
            if (username.equals(_user.username)) {
                usersList.remove(i);
                if (notify) {
                    usersListAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }
    public void removeUser(String username) {
        removeUser(username, true);
    }
    public void removeUser(User user, boolean notify) {
        removeUser(user.username, notify);
    }
    public void removeUser(User user) {
        removeUser(user, true);
    }
}
