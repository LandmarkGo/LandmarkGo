package com.OPSC.OPSC7312;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText user_name, pass_word;
    FirebaseAuth mAuth;

    ProgressBar circle;
    FirebaseUser user;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).hide();


        user_name = findViewById(R.id.email);
        pass_word = findViewById(R.id.password);
        Button btn_login = findViewById(R.id.btn_login);
        Button btn_sign = findViewById(R.id.btn_signup);

        //circle = findViewById(R.id.progress_circle);
        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth)
            {
                if (firebaseAuth.getCurrentUser() != null)
                {
                    Intent it = new Intent(Login.this, MapsActivity.class);
                    startActivity(it);
                }
            }
        });










        btn_login.setOnClickListener(v ->
        {
            String email = user_name.getText().toString().trim();
            String password = pass_word.getText().toString().trim();
            if (email.isEmpty()) {
                user_name.setError("Email is empty");
                user_name.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                user_name.setError("Enter the valid email");
                user_name.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                pass_word.setError("Password is empty");
                pass_word.requestFocus();
                return;
            }
            if (password.length() < 6) {
                pass_word.setError("Length of password is more than 6");
                pass_word.requestFocus();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    uid = user.getUid();


                    Intent intent
                            = new Intent(Login.this,
                            MapsActivity.class);

                    intent.putExtra("UID", uid);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(Login.this,
                            "Please Check Your login Credentials",
                            Toast.LENGTH_SHORT).show();
                }

            });
        });
        btn_sign.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
    }
}