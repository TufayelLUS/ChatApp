package me.thechoyon.chatapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends Activity {
    private FirebaseAuth user = FirebaseAuth.getInstance();
    private ClipboardManager clipboardManager;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final FirebaseUser logged_user = user.getCurrentUser();
        TextView user_screen = findViewById(R.id.user);
        final EditText mail_init = findViewById(R.id.mail_id);
        Button chatBtn = findViewById(R.id.start_chat);
        Button copy_id = findViewById(R.id.get_id);
        if (logged_user != null)
        {
            StringBuilder myStr = new StringBuilder(logged_user.getEmail());
            myStr.append("( ID: ");
            myStr.append(logged_user.getUid().substring(0,7));
            myStr.append(" )");
            user_screen.setText(myStr.toString());
            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mail_addr = mail_init.getText().toString();
                    if (TextUtils.isEmpty(mail_addr))
                    {
                        Toast.makeText(getApplicationContext(), "Please enter Unique ID correctly!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent init_a_chat = new Intent(ChatActivity.this, ChatMain.class);
                        init_a_chat.putExtra("from",mail_addr);
                        startActivity(init_a_chat);
                    }
                }
            });
            copy_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("content", logged_user.getUid().substring(0,7));
                    clipboardManager.setPrimaryClip(clip);
                    Toast.makeText(ChatActivity.this, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
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
                    startActivity(new Intent(ChatActivity.this, MainActivity.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (item.toString().equals("My Profile"))
        {
            startActivity(new Intent(ChatActivity.this, Profile.class));
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user_logged_in = user.getCurrentUser();
        if (user_logged_in == null)
        {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
        }
    }
}
