package com.tcpsocketclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProtocolParser implements Runnable {
    private SharedPreferences sp;
    private SharedPreferences.Editor spe;

    Handler handler = new Handler();

    public ProtocolParser(Context context) {
        sp = context.getSharedPreferences(MainActivity.APP_NAME, Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    @Override
    public void run() {
        final int port = sp.getInt("port", MainActivity.DEFAULT_PORT);
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                short opcode = dataInputStream.readShort(); // Opcode Server to Client
                final String username = dataInputStream.readUTF();
                if (opcode == MainActivity.OPCODE_STC_CONNECT) {
                    // User
                    final String clientIP = dataInputStream.readUTF();
                    final long lastActionTime = dataInputStream.readLong();

                    // UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.mainActivity.onLogin(username, clientIP, lastActionTime);
                        }
                    });

                } else if (opcode == MainActivity.OPCODE_STC_DISCONNECT) {
                    // Force back to main activity
                    Intent intent = new Intent(MainActivity.mainActivity.getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MainActivity.mainActivity.startActivity(intent);

                    // Load default values
                    MainActivity.mainActivity.loadDefaultValues();

                    // UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.mainActivity.onLogout();
                        }
                    });

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
