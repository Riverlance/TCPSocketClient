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
import android.widget.EditText;

import com.tcpsocketclient.tcpsocketclient.R;

public class MessageListActivity extends AppCompatActivity {
    // Views
    public EditText chatBoxEditText;

    // Needed stuffs
    public RecyclerView recyclerView;
    public MessagesListAdapter messagesListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private User targetUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("glauco");

        // Views
        chatBoxEditText = findViewById(R.id.chatBoxEditText);

        Intent intent = getIntent();
        if (intent != null) {
            String targetUsername = intent.getStringExtra("targetUsername");
            intent.removeExtra("targetUsername");

            targetUser = MainActivity.mainActivity.usersMap.get(targetUsername);
            if (targetUser == null) {
                finish();
                return;
            }
        }
        getSupportActionBar().setTitle(targetUser.username);

        /*
        ArrayList<Message> messages = new ArrayList<>();
        Message msg1 = new Message(), msg2 = new Message();
        msg1.username = "river";
        msg1.sentByMe = true;
        msg1.value = "Olá. Tudo bem?";
        messages.add(msg1);
        msg2.username = "glauco";
        msg2.value = "Estou bem sim. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        messages.add(msg2);
        */

        // Needed stuffs
        buildRecyclerView();

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
    protected void onStart() {
        super.onStart();

        MainActivity.messageListActivity = this;
    }

    @Override
    protected void onStop() {
        super.onStop();

        MainActivity.messageListActivity = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(0); // Don't close UsersListActivity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_message_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logoutItem:
                setResult(0); // Don't close UsersListActivity
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.messageListRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        messagesListAdapter = new MessagesListAdapter(targetUser.messages);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messagesListAdapter);

        // Set messages starting from bottom
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Not needed at the moment
        /*
        messagesListAdapter.setOnItemClickListener(new MessagesListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Toast.makeText(MessageListActivity.this, String.format("%d", position), Toast.LENGTH_SHORT).show();
                // If you change something in the selected data, use: usersListAdapter.notifyDataSetChanged();
            }
        });
        */
    }

    public void onClickFloatActionButton(View view) {
        String chatText = chatBoxEditText.getText().toString();
        if (chatText.equals(null) || chatText.equals("")) {
            Snackbar.make(view, "Digite alguma mensagem.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // To do: send message to destination
    }
}
