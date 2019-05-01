package com.tcpsocketclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tcpsocketclient.tcpsocketclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Constants
    public static final String APP_NAME = "TCP Socket Client";
    public static final int DEFAULT_PORT = 7171;
    public static final int DEFAULT_TIMETOKICK = 20000; // Has a copy on server
    // Opcodes (Operation Codes)
    // CTS - Client to Server
    public static final short OPCODE_CTS_SELFCONNECT = 1; // Request
    public static final short OPCODE_CTS_SELFDISCONNECT = 2; // Request
    public static final short OPCODE_CTS_UPDATEDUSERSLIST = 3; // Request
    // STC - Server to Client
    public static final short OPCODE_STC_SELFCONNECT = 1; // Answer
    public static final short OPCODE_STC_SELFDISCONNECT = 2; // Answer
    public static final short OPCODE_STC_UPDATEDUSERSLIST = 3; // Answer
    public static final short OPCODE_STC_FRIENDLOGGEDIN = 4; // Broadcast to friends that self logged in
    public static final short OPCODE_STC_FRIENDLOGGEDOUT = 5; // Broadcast to friends that self logged out

    // Needed stuffs
    public static MainActivity mainActivity;
    public static UsersListActivity usersListActivity;
    private SharedPreferences sp;
    private SharedPreferences.Editor spe;
    public static Map<String, User> usersMap = new HashMap<>();
    public ArrayList<User> chatsList = new ArrayList<>();
    public static Thread protocolParserThread;

    // Views
    public EditText usernameEditText;
    public EditText ipEditText;
    public EditText portEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Needed stuffs
        mainActivity = this;
        sp = getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        spe = sp.edit();
        // Client listening server
        if (protocolParserThread == null) {
            protocolParserThread = new Thread(new ProtocolParser());
            protocolParserThread.start();
        }

        // Get views
        usernameEditText = findViewById(R.id.usernameEditText);
        ipEditText = findViewById(R.id.ipEditText);
        portEditText = findViewById(R.id.portEditText);
        loadDefaultValues();
    }

    public void onClickConnectButton(View view) {
        String serverIP = ipEditText.getText().toString();
        String port = portEditText.getText().toString();
        String username = usernameEditText.getText().toString();

        // Saving data at app preferences
        spe.putString("serverIP", serverIP);
        spe.putInt("port", !port.equals("") ? Integer.parseInt(port) : DEFAULT_PORT);
        spe.putString("username", username);
        spe.commit();

        login();
    }


    public void localLogin(String username) {
        // Make sure username data is saved
        spe.putString("username", username);
        spe.commit();

        // Ensures that exists this user in usersMap
        if (!usersMap.containsKey(username)) {
            usersMap.put(username, new User(username));
        }

        // Get online user based on saved username
        User user = getOnlineUser();
        if (user == null) {
            return; // Never happens
        }

        Context context = getApplicationContext();

        // Open chat list activity
        if (usersListActivity == null) {
            Intent intent = new Intent(context, UsersListActivity.class);
            intent.putExtra("requestUpdatedUsersList", true);
            startActivity(intent);
        }

        // Message
        Toast.makeText(context, "Sua sessao iniciou.", Toast.LENGTH_SHORT).show();
    }

    public void login() {
        // Send login request
        ProtocolSender protocolSender = new ProtocolSender();
        protocolSender.execute(String.format("%d", OPCODE_CTS_SELFCONNECT));
    }

    public void localLogout() {
        // Get online user based on saved username
        User user = getOnlineUser();

        // If its online, remove it
        if (user != null) {
            usersMap.remove(user.username);
        }

        // Leave usersListActivity
        if (MainActivity.usersListActivity != null) {
            MainActivity.usersListActivity.finish();
        }

        // Message
        Context context = getApplicationContext();
        Toast.makeText(context, "Sua sessao encerrou.", Toast.LENGTH_SHORT).show();
    }

    public void logout() {
        ProtocolSender protocolSender = new ProtocolSender();
        protocolSender.execute(String.format("%d", OPCODE_CTS_SELFDISCONNECT));
    }

    public User getOnlineUser() {
        String username = sp.getString("username", "");
        return !username.equals("") ? usersMap.get(username) : null;
    }

    public void loadDefaultValues() {
        // Loading data from app preferences
        usernameEditText.setText(sp.getString("username", ""));
        ipEditText.setText(sp.getString("serverIP", ""));
        portEditText.setText(String.format("%d", sp.getInt("port", DEFAULT_PORT)));
    }

    /* Chat List */

    public int findChatUser(String username) {
        for (int i = 0; i < chatsList.size(); i++) {
            if (username.equals(chatsList.get(i).username)) {
                return i;
            }
        }
        return -1;
    }

    public void addChatUser(User user, boolean notify) {
        if (findChatUser(user.username) > -1) {
            return;
        }
        chatsList.add(user);
        if (notify && usersListActivity != null) {
            usersListActivity.usersListAdapter.notifyDataSetChanged();
        }
    }

    public void addChatUser(User user) {
        addChatUser(user, true);
    }

    public void removeChatUser(int i, boolean notify) {
        MainActivity.mainActivity.chatsList.remove(i);
        if (notify && usersListActivity != null) {
            usersListActivity.usersListAdapter.notifyDataSetChanged();
        }
    }

    public void removeChatUser(int i) {
        removeChatUser(i, true);
    }

    public void removeChatUser(String username, boolean notify) {
        int i = findChatUser(username);
        if (i == -1) {
            return;
        }
        removeChatUser(i, notify);
    }

    public void removeChatUser(String username) {
        removeChatUser(username, true);
    }

    public void removeChatUser(User user, boolean notify) {
        removeChatUser(user.username, notify);
    }

    public void removeChatUser(User user) {
        removeChatUser(user, true);
    }

    public void notifyChatUsersDataChanged() {
        if (usersListActivity != null) {
            usersListActivity.usersListAdapter.notifyDataSetChanged();
        }
    }
}
