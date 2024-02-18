package com.example.carpoolbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * this class displays all of the information of the user
 * it retrieves the information of the user from firestore
 * which is then displayed on the user profile page
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String doc;

    // button that the user can click to go back to the main page
    public void back(View view){
        Intent i = new Intent(this, MainActivity.class );
        startActivity(i);
    }

    /**
     * retrieves the users information from firebase
     * populates the corresponding UI elements with the relevant data
     * displays on the layout, creating the user profile screen
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        doc = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        firestore.collection("user-collection").document(doc).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot d = task.getResult();
                            TextView nDisplay = findViewById(R.id.name);
                            TextView eDisplay = findViewById(R.id.email);
                            TextView uDisplay = findViewById(R.id.userType);
                            if (d.exists()) {
                                nDisplay.setText(d.getString("name"));
                                eDisplay.setText("Email: "+ d.getString("email"));
                                uDisplay.setText("User Type: " + d.getString("userType"));
                            }
                        }
                    }
                });
    }
}