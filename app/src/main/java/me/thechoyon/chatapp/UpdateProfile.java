package me.thechoyon.chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UpdateProfile extends Activity {
    EditText disp_name;
    EditText email_addr;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    Button updateBtn;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        disp_name = findViewById(R.id.up_display);
        email_addr = findViewById(R.id.up_email);
        progressDialog = new ProgressDialog(this);
        updateBtn = findViewById(R.id.update_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String disp = user.getDisplayName();
            disp_name.setText(disp);
            email_addr.setText(user.getEmail());
            email_addr.setEnabled(false);
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String dat = disp_name.getText().toString();
                    try
                    {
                        if (!disp.equals(dat) && !TextUtils.isEmpty(dat.trim()))
                        {
                            progressDialog.setTitle("Updating Profile");
                            progressDialog.setMessage("Please wait while your profile is being updated ...");
                            progressDialog.show();
                            updateTheProfile(dat);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Display name cannot be empty!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateTheProfile(String dat) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(dat)
                .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UpdateProfile.this, MainActivity.class));
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
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            startActivity(new Intent(UpdateProfile.this, MainActivity.class));
        }
    }
}
