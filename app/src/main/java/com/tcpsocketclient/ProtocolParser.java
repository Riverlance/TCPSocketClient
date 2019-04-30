package com.tcpsocketclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ProtocolParser implements Runnable {
    // Needed stuffs
    private SharedPreferences sp;
    private SharedPreferences.Editor spe;
    // Allows to use UI within this background task
    // This is needed because this class is executed in another thread that is different of the main UI thread
    Handler handler = new Handler();

    public ProtocolParser() {
        // Needed stuffs
        sp = MainActivity.mainActivity.getSharedPreferences(MainActivity.APP_NAME, Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    @Override
    public void run() {
        // Params to listen the server
        final int port = sp.getInt("port", MainActivity.DEFAULT_PORT);

        try {
            // Socket connection
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                // Stream
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                // Basic data
                short opcode = dataInputStream.readShort(); // Opcode Server to Client
                final String username = dataInputStream.readUTF();

                if (opcode == MainActivity.OPCODE_STC_SELFCONNECT) {
                    // Execute in UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.mainActivity.localLogin(username);
                        }
                    });

                } else if (opcode == MainActivity.OPCODE_STC_SELFDISCONNECT) {
                    // Force back to main activity
                    /* *** This solution is not working properly because it recreates the main activity, instead back to the existing one. ***
                    Intent intent = new Intent(MainActivity.mainActivity.getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MainActivity.mainActivity.startActivity(intent);
                    */
                    //if (MainActivity.usersListActivity != null) {
                    //    MainActivity.usersListActivity.finish();
                    //}

                    // Execute in UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.mainActivity.localLogout();
                        }
                    });

                } else if (opcode == MainActivity.OPCODE_STC_UPDATEDUSERSLIST) {
                    int size = dataInputStream.readInt();

                    final ArrayList<String> tempUsernames = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        String _username = dataInputStream.readUTF();
                        tempUsernames.add(_username);
                    }

                    // Execute in UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Clear usersMap
                            MainActivity.mainActivity.usersMap = new HashMap<>();

                            for (String tempUsername : tempUsernames) {
                                User tempUser = new User(tempUsername);
                                MainActivity.mainActivity.usersMap.put(tempUsername, tempUser);

                                // Update chat (add missing users to chat list)
                                if (MainActivity.usersListActivity != null) {
                                    if (MainActivity.mainActivity.findChatUser(tempUsername) == -1) {
                                        MainActivity.mainActivity.addChatUser(tempUser, false);
                                    }
                                }
                            }
                            // Update chat (rows)
                            if (MainActivity.usersListActivity != null) {
                                for (int i = 0; i < MainActivity.mainActivity.chatsList.size(); i++) {
                                    String chatUsername = MainActivity.mainActivity.chatsList.get(i).username;
                                    boolean isOnline = MainActivity.mainActivity.usersMap.containsKey(chatUsername);
                                    MainActivity.usersListActivity.updateRow(i, isOnline);
                                }
                            }
                            MainActivity.mainActivity.notifyChatUsersDataChanged();
                        }
                    });

                } else if (opcode == MainActivity.OPCODE_STC_FRIENDLOGGEDIN) {
                    final String friendUsername = dataInputStream.readUTF();

                    final User tempUser = new User(friendUsername);

                    if (!MainActivity.usersMap.containsKey(friendUsername)) {
                        MainActivity.usersMap.put(friendUsername, tempUser);
                    }

                    // Execute in UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Add friend to chat list
                            MainActivity.mainActivity.addChatUser(tempUser);
                            // Toast.makeText(MainActivity.mainActivity.getApplicationContext(), String.format("My friend logged in: %s", friendUsername), Toast.LENGTH_SHORT).show();

                            int friendChatUserPos = MainActivity.mainActivity.findChatUser(friendUsername);
                            if (friendChatUserPos != -1) {
                                MainActivity.usersListActivity.updateRow(friendChatUserPos, true);
                            }
                        }
                    });

                } else if (opcode == MainActivity.OPCODE_STC_FRIENDLOGGEDOUT) {
                    final String friendUsername = dataInputStream.readUTF();

                    final User friendUser = MainActivity.usersMap.get(friendUsername);
                    if (friendUser != null) {
                        MainActivity.usersMap.remove(friendUsername);
                    }

                    // Execute in UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Remove friend from chat list (we don't want this, so leave it commented)
                            // MainActivity.mainActivity.removeChatUser(friendUsername);
                            // Toast.makeText(MainActivity.mainActivity.getApplicationContext(), String.format("My friend logged out: %s", friendUsername), Toast.LENGTH_SHORT).show();

                            int friendChatUserPos = MainActivity.mainActivity.findChatUser(friendUsername);
                            if (friendChatUserPos != -1) {
                                MainActivity.usersListActivity.updateRow(friendChatUserPos, false);
                            }
                        }
                    });
                }
            }

            // Never happens because Client is always listening
            // dataInputStream.close();
            // serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
