package com.example.carpoolbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Objects;
import java.util.UUID;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * info is filled in by the user to create a new vehicle
 * the added vehicle is updated on firebase
 *
 * @author Gigi Xiao
 * @version 0.0
 */
public class AddVehicleActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Spinner vehicle;
    private EditText model;
    private EditText capacity;
    private EditText price;
    private EditText location;

    //button to let user go back to the main activity page
    public void Back(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * method will set up the sign in and firebase authentication
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        vehicle = findViewById(R.id.vehicleTypeSpinner);
        model = findViewById(R.id.modelInsert);
        capacity = findViewById(R.id.capacityInsert);
        price = findViewById(R.id.priceInsert);
        location = findViewById(R.id.addressInsert);
    }

    /**
     * the user adds a new vehicle, choosing the vehicle type from the spinner object
     * it is added to the user/owner
     * user is then brought back to the main page once the vehicle is added
     *
     * @param view the button clicked by the user
     */
    public void addNewVehicle(View view){
        if(vehicle != null && model != null && capacity != null && price != null && location != null){
            String s = ((String) vehicle.getSelectedItem());

            Vehicle newV = null;
            if (s.equals("Car")) {
                newV = new Car();
            } else if (s.equals("Helicopter")){
                newV = new Helicopter();
            } else if (s.equals("Bicycle")){
                newV = new Bicycle();
            } else if (s.equals("Segway")){
                newV = new Segway();
            }
            Vehicle finalNewV = newV;
            firestore.collection("user-collection").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot d = task.getResult();
                        User myUser = d.toObject(User.class);

                        assert finalNewV != null;
                        assert myUser != null;

                        finalNewV.setOpen(true);
                        finalNewV.setOwner(myUser.getName());
                        finalNewV.setVehicleType(s);
                        finalNewV.setModel(model.getText().toString());
                        finalNewV.setVehicleID(String.valueOf(UUID.randomUUID()));
                        finalNewV.setBasePrice(Double.parseDouble(price.getText().toString()));
                        int capacityValue = (Integer.parseInt(capacity.getText().toString()));
                        finalNewV.setCapacity(capacityValue);
                        finalNewV.setSeatsLeft(capacityValue);

                        firestore.collection("vehicle-collection").document(finalNewV.getVehicleID()).set(finalNewV);
                        Toast.makeText(AddVehicleActivity.this, "New vehicle added.", Toast.LENGTH_SHORT).show();
                }
            });
            //bring user back to main page after successfully creating the new vehicle
            Intent i = new Intent(this, MainActivity.class );
            startActivity(i);
        }
    }
}