package com.example.carpoolbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

/**
 * this class displays the information/details of a vehicle
 * user views this page after clicking on a vehicle in VehiclesInfoActivity
 * user able to reserve the vehicle here
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class VehicleProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private String documentId;
    private Long seatsLeft;

    // button to go back to the vehicle info page
    public void Back(View view){
        Intent i = new Intent(this, VehiclesInfoActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        firestore = FirebaseFirestore.getInstance();
        documentId = getIntent().getStringExtra("documentId");

        retrieveVehicleDetails();
    }

    /**
     * method that retrieves the vehicle information from firebase
     */
    private void retrieveVehicleDetails() {
        firestore.collection("vehicle-collection").document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot d = task.getResult();
                            if (d.exists()) {
                                updateInfo(d);
                            }
                        }
                    }
                });
    }

    /**
     * updates the information in the screen
     * @param d is a snapshot of the document details in firebase
     *          in which it can extract the values from it and be used to populate the UI elements
     */
    private void updateInfo(DocumentSnapshot d) {
        // Update owner information
        String owner = d.getString("owner");
        TextView ownerTextView = findViewById(R.id.owner);
        ownerTextView.setText(owner);

        // Update base price information
        Long basePrice = d.getLong("basePrice");
        TextView basePriceTextView = findViewById(R.id.basePrice);
        basePriceTextView.setText(basePrice != null ? basePrice.toString() : "");

        // Update capacity information
        Long capacity = d.getLong("capacity");
        TextView capacityTextView = findViewById(R.id.capacity);
        capacityTextView.setText(capacity != null ? capacity.toString() : "");

        // Update model information
        String model = d.getString("model");
        TextView modelTextView = findViewById(R.id.model);
        modelTextView.setText(model);

        // Update vehicle type information
        String vehicleType = d.getString("vehicleType");
        TextView vehicleTypeTextView = findViewById(R.id.vehicleType);
        vehicleTypeTextView.setText(vehicleType);

        // Update seats left information
        seatsLeft = d.getLong("seatsLeft");
        TextView seatsLeftTextView = findViewById(R.id.seatsLeft);
        seatsLeftTextView.setText(seatsLeft != null ? seatsLeft.toString() : "");
    }

    /**
     * the user clicks the button to reserve a ride
     * the seats left value will decrease - if it's at 1, then the vehicle will also be closed
     * as the seat value would become zero
     * the user is brought back to the previous page, vehicles info
     *
     * @param view user clicks a button
     */
    public void bookRide(View view) {
        firestore.collection("vehicle-collection").document(documentId)
                .update("seatsLeft", FieldValue.increment(-1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        String message = "Successfully booked.";
                        Toast.makeText(VehicleProfileActivity.this, message, Toast.LENGTH_SHORT).show();

                        //when number of seats is 1, gets booked, becomes 0, vehicle is closed
                        if (seatsLeft == 1) {
                            closeVehicle();
                        }

                        //bring user back to vehicles info page
                        Intent i = new Intent(VehicleProfileActivity.this, VehiclesInfoActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = "Booking failed. Try again.";
                        Toast.makeText(VehicleProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // sets a vehicle's status to be closed
    private void closeVehicle() {
        firestore.collection("vehicle-collection").document(documentId).update("open", false);
    }
}