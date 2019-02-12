package me.thechoyon.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends Activity {
    EditText email_add;
    EditText pass_1;
    EditText pass_2;
    FirebaseAuth mAuth;
    Button signUpBtn;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait ...");
        dialog.setCancelable(false);
        dialog.setMessage("Creating account ...");
        email_add = findViewById(R.id.email1);
        pass_1 = findViewById(R.id.pass1);
        pass_2 = findViewById(R.id.pass2);
        signUpBtn = findViewById(R.id.signUpStart);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_add.getText().toString().trim();
                String pass1 = pass_1.getText().toString().trim();
                String pass2 = pass_2.getText().toString().trim();
                String [] arr = email.split("@");
                if (arr.length == 2)
                {
                    if (arr[0].trim().length() == 0 || arr[1].trim().length() == 0 || (!arr[1].contains(".") && arr[1].length() < 4) || arr[1].endsWith("."))
                    {
                        email_add.setError("Email format incorrect!");
                        email_add.requestFocus();
                        return;
                    }
                }
                else
                {
                    email_add.setError("Email format incorrect!");
                    email_add.requestFocus();
                    return;
                }
                if (pass1.length() < 6)
                {
                    pass_1.setError("Minimum password length is 6!");
                    pass_1.requestFocus();
                    return;
                }
                if (pass2.length() < 6)
                {
                    pass_2.setError("Minimum password length is 6!");
                    pass_2.requestFocus();
                    return;
                }
                dialog.show();
                signUpUser(email, pass1, pass2);
            }

        });
    }

    private void signUpUser(String email, String pass1, String pass2) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass1)
                && !TextUtils.isEmpty(pass2))
        {
            if (pass1.equals(pass2))
            {
                mAuth.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(
                        this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    dialog.dismiss();
                                    startActivity(new Intent(SignUpActivity.this, ChatActivity.class));
                                }
                                else
                                {
                                    dialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Error! Maybe there already exists an account with this mail! Try another.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
            else
            {
                pass_1.setText("");
                pass_2.setText("");
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Ooops! Password mismatch!", Toast.LENGTH_SHORT).show();
                pass_1.requestFocus();
            }
        }
        else
        {
            dialog.dismiss();
            Toast.makeText(SignUpActivity.this, "Please fill in details correctly!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user_logged_in = mAuth.getCurrentUser();
        if (user_logged_in != null)
        {
            startActivity(new Intent(SignUpActivity.this, ChatActivity.class));
        }
    }
}
