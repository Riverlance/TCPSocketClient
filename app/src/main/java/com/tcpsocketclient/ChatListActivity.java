package com.tcpsocketclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tcpsocketclient.tcpsocketclient.R;

public class ChatListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        User user = MainActivity.mainActivity.loggedUser;
        if (user != null) {
            getSupportActionBar().setTitle(user.username);
        }

        // First onTick instantly
        onTick();

        // Executes periodically, once per second
        Thread ticksThread = new Thread() {
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
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ticksThread.start();
    }

    private void onTick() {
        User user = MainActivity.mainActivity.loggedUser;
        if (user == null) {
            return;
        }

        ProtocolSender protocolSender = new ProtocolSender();
        //protocolSender.execute(String.format("%d", MainActivity.OPCODE_CTS_USERSMAPSIGNAL));
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
                Toast.makeText(this, "q", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
