package com.example.medicharm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    TextView Login;
    EditText Email, Password, Conform_Password;
    Button Register;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Email = findViewById(R.id.signup_emial);
        Password = findViewById(R.id.signup_password);
        Conform_Password = findViewById(R.id.signup_conform_password);
        Register = findViewById(R.id.register_buttion);
        Login = findViewById(R.id.login_text);
        progressBar = findViewById(R.id.loader);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    void createAccount() {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String conform_password = Conform_Password.getText().toString().trim();
        if (!Validate(email, password, conform_password)) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        createAccountInFireBase(email, password);
    }

    void createAccountInFireBase(String email, String password) {
        FirebaseAuth fb = FirebaseAuth.getInstance();
        fb.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Account Created Successfully Check Your Mail", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    boolean Validate(String email, String password, String conform_password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Email Is Invalid");
            return false;
        }
        if (password.length() < 6) {
            Password.setError("Length Is Invalid");
            return false;
        }
        if (!password.equals(conform_password)) {
            Conform_Password.setError("Passwords Not Match");
            return false;
        }
        return true;
    }
}
