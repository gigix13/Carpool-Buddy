package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * the sign up process/page for new users
 * spinner object for them to pick what user type (student, teacher, etc)
 * the new user is added to firebase
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Spinner userType;
    private EditText email;
    private EditText password;
    private EditText name;

    /**
     * sets up UI components and initialises the variables needed
     * sets adapter for the spinner view
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
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userType = findViewById(R.id.userTypeSpinner);
        name = findViewById(R.id.nameInfo);
        email = findViewById(R.id.emailInfo);
        password = findViewById(R.id.passwordInfo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.userTypes , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
    }

    /**
     * user picks a user type from the spinner
     * fills in the fields for name and email, which is reflected in firebase data
     * user brought to main page if the sign up was successful
     *
     * @param view user clicks on a button/picks the user type from the spinner
     */
    public void signUp(View view){
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String s = userType.getSelectedItem().toString();
                    User newUser = null;
                    if (s.equals("Student")) {
                        newUser = new Student();
                    } else if (s.equals("Alumni")) {
                        newUser = new Alumni();
                    } else if (s.equals("Teacher")) {
                        newUser = new Teacher();
                    } else if (s.equals("Parent")) {
                        newUser = new Parent();
                    }

                    //making the new user in firebase
                    assert newUser != null;
                    newUser.setUserType(s);
                    newUser.setName(name.getText().toString());
                    newUser.setEmail(email.getText().toString());
                    newUser.setUid(mAuth.getUid());

                    firestore.collection("user-collection").document(newUser.getUid()).set(newUser);
                    Toast.makeText(SignUp.this, "Successfully signed up.", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else {
                    Toast.makeText(SignUp.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }
    // if successful, user brought to main page
    public void updateUI(FirebaseUser newUser){
        if(newUser != null){
            Intent i = new Intent(this, MainActivity.class );
            startActivity(i);
        }
    }
}