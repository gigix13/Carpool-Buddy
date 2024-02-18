package com.example.carpoolbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * this class is a page to look at all the available/open vehicles.
 * then clicking into one of the vehicles on the page, the user will be brought to the VehicleProfileActivity page
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class VehiclesInfoActivity extends AppCompatActivity {
    private ArrayList<String> vIDList = new ArrayList<>();
    private ArrayAdapter<String> vListAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    //method for the button which goes back to the main page
    public void Back(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /**
     * the creative feature: the user can filter through the open vehicles
     *       depending on how many seats are available
     *       checks through comparing the seats left with the value of the filter's first character
     * uses a spinner for the options of the filter
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Spinner filterButton = findViewById(R.id.seatFilter);
        //adding the options for the filter spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.add("All Vehicles");
        spinnerAdapter.add("1+ Seat(s) Available");
        spinnerAdapter.add("2+ Seats Available");
        spinnerAdapter.add("3+ Seats Available");
        spinnerAdapter.add("4+ Seats Available");
        filterButton.setAdapter(spinnerAdapter);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ListView vList = findViewById(R.id.allVehicles);
        ArrayList<String> vInfoList = new ArrayList<>();
        ArrayList<String> vContentList = new ArrayList<>();
        ArrayList<String> vIDList = new ArrayList<>();

        ArrayAdapter<String> vListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, vInfoList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View view, @NonNull ViewGroup p) {
                if (view == null) {
                    view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, p, false);
                }
                String i = getItem(position);
                String sItem = vContentList.get(position);

                TextView all = view.findViewById(android.R.id.text1);
                all.setText(i);
                TextView sub = view.findViewById(android.R.id.text2);
                sub.setText(sItem);

                return view;
            }
        };
        vList.setAdapter(vListAdapter);

        vList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                String selectedItem = (String) vIDList.get(pos);

                firestore.collection("vehicle-collection")
                        .whereEqualTo("vehicleID", selectedItem).limit(1).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    String documentId = task.getResult().getDocuments().get(0).getId();
                                    Intent i = new Intent(VehiclesInfoActivity.this, VehicleProfileActivity.class);
                                    i.putExtra("documentId", documentId);
                                    startActivity(i);
                                }
                            }
                        });
            }
        });

        filterButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View view, int pos, long id) {
                String selectedFilter = (String) p.getItemAtPosition(pos);

                // Convert the selectedFilter string to an integer using ASCII
                int filterValue = (((int) selectedFilter.charAt(0)) - 48);
                //debugging
                System.out.println("Filter value: " + filterValue);

                firestore.collection("vehicle-collection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            vInfoList.clear();
                            vContentList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String vehicleType = document.getString("vehicleType");
                                String owner = document.getString("owner");

                                boolean open = Boolean.TRUE.equals(document.getBoolean("open"));
                                String subItem = String.valueOf(document.getLong("seatsLeft")) + " Seat(s) Left";

                                // Create an instance of the Vehicle class
                                Vehicle vehicle = document.toObject(Vehicle.class);
                                // Call getSeatsLeft() on the vehicle instance
                                int nSeatsLeft = vehicle.getSeatsLeft();
                                //debugging
                                System.out.println("Seats left: " + nSeatsLeft);

                                if (open && owner != null && (nSeatsLeft >= filterValue || selectedFilter.equals("All Vehicles"))) {
                                    vInfoList.add(owner);
                                    vContentList.add(subItem);
                                    vIDList.add(document.getString("vehicleID"));
                                }
                            }
                            vListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });
    }
}
