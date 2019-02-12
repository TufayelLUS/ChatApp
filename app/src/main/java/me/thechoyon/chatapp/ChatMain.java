package me.thechoyon.chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class ChatMain extends Activity {
    private FirebaseAuth user = FirebaseAuth.getInstance();
    private DatabaseReference myRef;
    Bundle b;
    EditText msg_body;
    AlertDialog.Builder builder;
    Button sendBtn;
    ArrayAdapter<String> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        msg_body = findViewById(R.id.msg_body_1);
        sendBtn = findViewById(R.id.send_msg);
        listView = findViewById(R.id.mylistview);
        myRef = FirebaseDatabase.getInstance().getReference().child("chat");
        b = getIntent().getExtras();
        if (b != null && user.getCurrentUser() != null)
        {
            final String chat_with = b.getString("from");
            final String retrieve_key = chat_with + "**" + user.getCurrentUser().getUid().substring(0,7);
            final String uniqueIdentifier = user.getCurrentUser().getUid().substring(0,7) + "**" + chat_with;
            myRef.child(retrieve_key).getParent().limitToFirst(50).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> myAr = new ArrayList<>();
                    for (DataSnapshot shots : dataSnapshot.getChildren()
                         ) {
                        if(shots.getKey().startsWith(uniqueIdentifier)) {
                            String content = "Me@Friend\n";
                            content += shots.getValue(String.class);
                            myAr.add(content);
                        }
                        else if (shots.getKey().startsWith(retrieve_key))
                        {
                            String content = "Friend@Me\n";
                            content += shots.getValue(String.class);
                            myAr.add(content);
                        }
                    }
                    adapter = new ArrayAdapter<>(ChatMain.this, android.R.layout.simple_list_item_1, myAr);
                    listView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(msg_body.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(), "No messages written!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        myRef.child(uniqueIdentifier).setValue(msg_body.getText().toString());
                        Toast.makeText(getApplicationContext(), "Sent to user!", Toast.LENGTH_SHORT).show();
                        msg_body.setText("");
                        msg_body.requestFocus();
                    }

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.toString().equals("Logout"))
        {
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Exit");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("Are you sure to logout?");
            builder.setCancelable(false);
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    user.signOut();
                    startActivity(new Intent(ChatMain.this, MainActivity.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user_logged_in = user.getCurrentUser();
        if (user_logged_in == null)
        {
            startActivity(new Intent(ChatMain.this, MainActivity.class));
        }
    }
}