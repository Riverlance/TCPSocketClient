package com.tcpsocketclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.tcpsocketclient.tcpsocketclient.R;

import java.util.ArrayList;

public class MessageListActivity extends AppCompatActivity {
    // Needed stuffs
    public RecyclerView recyclerView;
    public RecyclerView.Adapter messagesListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("glauco");

        ArrayList<Message> messages = new ArrayList<>();
        Message msg1 = new Message(), msg2 = new Message();
        msg1.username = "river";
        msg1.sentByMe = true;
        msg1.value = "Olá. Tudo bem?";
        messages.add(msg1);
        msg2.username = "glauco";
        msg2.value = "Estou bem sim. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        messages.add(msg2);

        messages.add(msg1);
        messages.add(msg2);
        messages.add(msg1);
        messages.add(msg2);
        messages.add(msg1);
        messages.add(msg2);
        messages.add(msg1);
        messages.add(msg2);

        // Needed stuffs
        recyclerView = findViewById(R.id.messageListRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        messagesListAdapter = new MessagesListAdapter(messages);

        // Needed stuffs
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messagesListAdapter);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_message_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
