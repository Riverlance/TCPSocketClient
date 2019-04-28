package com.tcpsocketclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class ProtocolSender extends AsyncTask<String, Void, String> { // <Params, Progress, Result>
    private SharedPreferences sp;

    Handler handler;

    public ProtocolSender() {
        sp = MainActivity.mainActivity.getSharedPreferences(MainActivity.APP_NAME, Context.MODE_PRIVATE);

        handler = new Handler();
    }

    @Override
    protected String doInBackground(String... strings) {
        short opcode = Short.parseShort(strings[0]);
        final String serverIP = sp.getString("serverIP", "");
        final int port = sp.getInt("port", MainActivity.DEFAULT_PORT);
        final String username = sp.getString("username", "");

        try {
            Socket socket = new Socket(serverIP, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeShort(opcode);
            dataOutputStream.writeUTF(username);

            if (opcode == MainActivity.OPCODE_CTS_CONNECT) {
                dataOutputStream.writeUTF(Utils.getIPAddress()); // Self IPv4 (Client)
            } else if (opcode == MainActivity.OPCODE_CTS_DISCONNECT) {
            } else if (opcode == MainActivity.OPCODE_CTS_USERSMAPSIGNAL) {
            }

            dataOutputStream.close();
            socket.close();
        } catch (ConnectException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.mainActivity.getApplicationContext(), String.format("Servidor nao encontrado.\nIP: %s (%d)\nUser: %s", serverIP, port, username), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* Not needed
    @Override
    protected void onPostExecute(String string) {
    }
    */
}
