package me.thechoyon.chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Profile extends Activity {

    TextView display_name;
    TextView mail_address;
    FirebaseUser cur_user = FirebaseAuth.getInstance().getCurrentUser();
    TextView uid;
    ImageView profile_pic;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_pic = findViewById(R.id.profile_img);
        display_name = findViewById(R.id.disp_name);
        mail_address = findViewById(R.id.email_addr);
        uid = findViewById(R.id.uid);
        if (cur_user != null)
        {
            profile_pic.setImageURI(cur_user.getPhotoUrl());
            display_name.setText(cur_user.getDisplayName());
            mail_address.setText(cur_user.getEmail());
            uid.setText(cur_user.getUid().substring(0,7));
        }
        else
        {
            startActivity(new Intent(Profile.this, MainActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_profile, menu);
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
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Profile.this, MainActivity.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    //
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (item.toString().equals("Update Profile"))
        {
            startActivity(new Intent(Profile.this, UpdateProfile.class));
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
