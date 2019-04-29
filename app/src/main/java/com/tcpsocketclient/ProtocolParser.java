package com.tcpsocketclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ProtocolParser implements Runnable {
    private SharedPreferences sp;
    private SharedPreferences.Editor spe;

    Handler handler = new Handler();

    public ProtocolParser() {
        sp = MainActivity.mainActivity.getSharedPreferences(MainActivity.APP_NAME, Context.MODE_PRIVATE);
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
                    // UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.mainActivity.onLogin(username);
                        }
                    });

                } else if (opcode == MainActivity.OPCODE_STC_DISCONNECT) {
                    // Force back to main activity
                    /* *** This solution is not working properly because it recreates the main activity, instead back to the existing one. ***
                    Intent intent = new Intent(MainActivity.mainActivity.getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    MainActivity.mainActivity.startActivity(intent);
                    */
                    if (MainActivity.usersListActivity != null) {
                        MainActivity.usersListActivity.finish();
                    }

                    // Load default values
                    MainActivity.mainActivity.loadDefaultValues();

                    // UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.mainActivity.onLogout();
                        }
                    });

                } else if (opcode == MainActivity.OPCODE_STC_USERSMAPSIGNAL) {
                    int size = dataInputStream.readInt();

                    for (int i = 0; i < size; i++) {
                        String _username = dataInputStream.readUTF();
                        User user;

                        // If doesn't have this user at usersMap, include it
                        if (!MainActivity.usersMap.containsKey(_username)) {
                            user = new User(_username);
                            MainActivity.usersMap.put(_username, user);
                        } else {
                            user = MainActivity.usersMap.get(_username);
                        }

                        if (MainActivity.usersListActivity != null) {

                        }
                    }

                    // TO DO
                    /*
                    UsersListAdapter usersListAdapter = null;
                    List<User> list = null;
                    if (MainActivity.usersListActivity != null) {
                        //usersListAdapter = MainActivity.usersListActivity.usersListAdapter;
                        //list = usersListAdapter.list;
                    }

                    boolean dataHasChanged = false;
                    for (int i = 0; i < size; i++) {
                        String _username = dataInputStream.readUTF();
                        User user;

                        // If doesn't have this user at usersMap, include it
                        if (!MainActivity.usersMap.containsKey(_username)) {
                            user = new User(_username);
                            MainActivity.usersMap.put(_username, user);
                            System.out.println(String.format("[FALSE] User: %s", user.username));
                        } else {
                            user = MainActivity.usersMap.get(_username);
                            System.out.println(String.format("[TRUE] User: %s", user.username));
                        }

                        if (MainActivity.usersListActivity != null) {
                            int chatListUser = -1;
                            for (int j = 0; i < list.size(); i++) {
                                if (_username.equals(list.get(j).username)) {
                                    chatListUser = j;
                                    break;
                                }
                            }

                            // Not found at chatList
                            if (chatListUser == -1) {
                                //list.add(user);
                                //dataHasChanged = true;
                                Intent intent = new Intent(MainActivity.mainActivity, UsersListActivity.class);
                                intent.putExtra("username", _username);
                                MainActivity.mainActivity.startActivityForResult(intent, 1);

                            // Found at chatList
                            } else {
                                // Update online status
                            }
                        }
                    }

                    if (dataHasChanged) {
                        //usersListAdapter.notifyDataSetChanged();
                    }
                    */
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
