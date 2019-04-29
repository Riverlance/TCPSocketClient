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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String APP_NAME = "TCP Socket Client";
    public static final int DEFAULT_PORT = 7171;

    // Opcodes (Operation Codes)
    // CTS - Client to Server
    public static final short OPCODE_CTS_CONNECT = 1;
    public static final short OPCODE_CTS_DISCONNECT = 2;
    public static final short OPCODE_CTS_USERSMAPSIGNAL = 3;
    // STC - Server to Client
    public static final short OPCODE_STC_CONNECT = 1;
    public static final short OPCODE_STC_DISCONNECT = 2;
    public static final short OPCODE_STC_USERSMAPSIGNAL = 3;

    public static MainActivity mainActivity;
    public static UsersListActivity usersListActivity;
    private SharedPreferences sp;
    private SharedPreferences.Editor spe;

    public EditText usernameEditText;
    public EditText ipEditText;
    public EditText portEditText;

    public static Map<String, User> usersMap = new HashMap<>();
    public static Thread protocolParserThread;

    public User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        sp = getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        spe = sp.edit();

        usernameEditText = findViewById(R.id.usernameEditText);
        ipEditText = findViewById(R.id.ipEditText);
        portEditText = findViewById(R.id.portEditText);

        loadDefaultValues();

        if (protocolParserThread == null) {
            protocolParserThread = new Thread(new ProtocolParser());
            protocolParserThread.start();
        }
    }

    public void onLogin(String username) {
        Context context = getApplicationContext();

        if (MainActivity.usersMap.containsKey(username)) {
            MainActivity.mainActivity.loggedUser = MainActivity.usersMap.get(username);
        } else {
            MainActivity.mainActivity.loggedUser = new User(username);
            MainActivity.usersMap.put(username, MainActivity.mainActivity.loggedUser);
        }

        // Open chat list activity
        startActivity(new Intent(context, UsersListActivity.class));

        // Message
        Toast.makeText(context, "Sua sessao iniciou.", Toast.LENGTH_SHORT).show();
    }

    public void onLogout() {
        Context context = getApplicationContext();

        // Message
        Toast.makeText(context, "Sua sessao encerrou.", Toast.LENGTH_SHORT).show();
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

        ProtocolSender protocolSender = new ProtocolSender();
        protocolSender.execute(String.format("%d", OPCODE_CTS_CONNECT));
    }



    public void loadDefaultValues() {
        // Loading data from app preferences
        usernameEditText.setText(sp.getString("username", ""));
        ipEditText.setText(sp.getString("serverIP", ""));
        portEditText.setText(String.format("%d", sp.getInt("port", DEFAULT_PORT)));
    }
}
