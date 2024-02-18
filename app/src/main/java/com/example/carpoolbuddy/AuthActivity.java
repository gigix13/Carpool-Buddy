package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * the sign in process for the user
 * authenticates the user via firebase
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class AuthActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    //button to go to sign up page instead
    public void signUp(View view){
        Intent i = new Intent(this, SignUp.class );
        startActivity(i);
    }

    /**
     * email and password is filled in by user
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_activity);

        email = findViewById(R.id.emailInsert);
        password = findViewById(R.id.passwordInsert);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * authenticates user using the provided email and password
     * displays the success/failure message using Toast
     * will bring the user to the main activity page if sign in is successful
     *
     * @param view button clicked by user
     */
    public void signIn(View view){
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String message = String.format("Signed in as %s", email.getText().toString());
                    Toast.makeText(AuthActivity.this, String.format("Signed in as %s", email.getText().toString()), Toast.LENGTH_SHORT).show();
                    updateUI(user);
                }
                else {
                    String message = "Signed in failed.";
                    Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }
    public void updateUI(FirebaseUser user){
        if(user != null){
            Intent i = new Intent(this, MainActivity.class );
            startActivity(i);
        }
    }
}