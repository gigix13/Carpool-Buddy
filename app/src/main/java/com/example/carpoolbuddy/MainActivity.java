package com.example.carpoolbuddy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * this class is the main page after the user signs in/up
 * their vehicles are displayed
 * there are options to go to other pages:
 * adding new vehicle, going to user profile, going to all vehicles, etc
 *
 * @author Gigi Xiao
 * @version 0.0
 */

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> vehicleInfoList = new ArrayList<>();
    private ArrayAdapter<String> vListAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String name;

    /**
    the buttons on the screen to bring user to different pages
    adding a new vehicle - AddVehicleActivity
    going to the user info page - UserProfileActivity
    going to the vehicle details - VehiclesInfoActivity
    logging out - AuthActivity
     */

    public void addNewVehicle(View view){
        Intent i = new Intent(this, com.example.carpoolbuddy.AddVehicleActivity.class );
        startActivity(i);
    }
    public void userInfo(View view){
        Intent i = new Intent(this, UserProfileActivity.class );
        startActivity(i);
    }

    public void vehicle(View view){
        Intent i = new Intent(this, VehiclesInfoActivity.class );
        startActivity(i);
    }

    public void signOut(View view){
        Intent i = new Intent(this, AuthActivity.class );
        startActivity(i);
    }

    /**
     * initialises mAuth and firestore instances
     * creates list view with an adapter, retrieves user information for the list
     * sets click listener for the vehicles in the list (check if user clicks)
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ListView vListView = findViewById(R.id.allVehicles);
        ArrayList<String> vehicleInfoList = new ArrayList<>();
        ArrayList<String> vIDs = new ArrayList<>();
        ArrayList<String> subItems = new ArrayList<>();
        ArrayList<Boolean> openList = new ArrayList<>();
        ArrayAdapter<String> vListAdapter = createListAdapter(vehicleInfoList, subItems);
        vListView.setAdapter(vListAdapter);

        retrieveUserInfo(vehicleInfoList, subItems, vIDs, openList, vListAdapter);
        setClickListenerForVehicles(vListView, vIDs, openList, vListAdapter, subItems);
    }

    /**
     * assigns the corresponding data from the vehicleInfoList and subItems to the list of vehicles
       displayed / the text views, using view recycling
     *
     * @param vehicleInfoList the list of vehicles / their owner
     * @param subItems the details of the vehicle: status + seats left
     * @return an array adapter, customised to display a list of the vehicles + the details
     */
    private ArrayAdapter<String> createListAdapter(ArrayList<String> vehicleInfoList, ArrayList<String> subItems) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, vehicleInfoList) {
            @NonNull
            @Override
            public View getView(int pos, @Nullable View v, @NonNull ViewGroup p) {
                ViewHolder viewHolder;

                if (v == null) {
                    v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, p, false);

                    viewHolder = new ViewHolder();
                    viewHolder.main = v.findViewById(android.R.id.text1);
                    viewHolder.sub = v.findViewById(android.R.id.text2);

                    v.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) v.getTag();
                }
                String mainT = getItem(pos);
                viewHolder.main.setText(mainT);
                String subT = subItems.get(pos);
                viewHolder.sub.setText(subT);

                return v;
            }
        };
    }

    private static class ViewHolder {
        TextView main;
        TextView sub;
    }

    /**
     * fetches user info from firebase based on the user ID
     * gets the user's name from firebase, calls retrieveVehiclesInfo to populate the vehicle information
     *
     * @param vehicleInfoList vehicles / their owners
     * @param subItems info of vehicles: status + seats left
     * @param vIDs vehicle ID in firebase
     * @param openList list of all open vehicles available for booking
     * @param vListAdapter the adapter between the list view and the data from vehicleInfoList ad subItems
     */
    private void retrieveUserInfo(ArrayList<String> vehicleInfoList, ArrayList<String> subItems, ArrayList<String> vIDs, ArrayList<Boolean> openList, ArrayAdapter<String> vListAdapter) {
        firestore.collection("user-collection").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot d = task.getResult();
                            if (d.exists() && d != null) {
                                String name = d.getString("name");
                                retrieveVehiclesInfo(name, vehicleInfoList, subItems, vIDs, openList, vListAdapter);
                            }
                        }
                    }
                });
    }

    /**
     * gets information from firebase about a vehicles details based on the owners/users name
     * extracts relevant data (name, status, seats left)
     *
     * @param name of the user / owner
     * @param vehicleInfoList info of vehicles stored in firebase
     * @param subItems sub-details corresponding to each vehicle
     * @param vIDs list of vehicle IDs
     * @param openList list of vehicle status's
     * @param vListAdapter anages data displayed in the list view
     */
    private void retrieveVehiclesInfo(String name, ArrayList<String> vehicleInfoList, ArrayList<String> subItems, ArrayList<String> vIDs, ArrayList<Boolean> openList, ArrayAdapter<String> vListAdapter) {
        firestore.collection("vehicle-collection").whereEqualTo("owner", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    vehicleInfoList.clear();
                    subItems.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String vInfo = document.getString("vehicleType");
                        boolean open = Boolean.TRUE.equals(document.getBoolean("open"));
                        String text = open ? "Open - " : "Closed - ";
                        String subItem = text + "Seat(s) left: " + String.valueOf(document.getLong("seatsLeft"));
                        vehicleInfoList.add(vInfo);
                        subItems.add(subItem);
                        vIDs.add(document.getString("vehicleID"));
                        openList.add(open);
                    }
                    vListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * this method checks for when a user clicks on a vehicle in the list
     * when it is clicked the vehicle's specific information is retrieved
     * updates the user interface to reflect any changes to status, etc
     *
     * @param vListView represents the list view
     * @param vIDs list of the vehicle IDs
     * @param openList list of the vehicles status's: open or closed
     * @param vListAdapter manages data displayed in the list view
     * @param subItems details of the vehicles displayed in the list view
     */
    private void setClickListenerForVehicles(ListView vListView, ArrayList<String> vIDs, ArrayList<Boolean> openList, ArrayAdapter<String> vListAdapter, ArrayList<String> subItems) {
        vListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = vIDs.get(position);
                boolean newV = !openList.get(position);

                updateVehicleStatus(selectedItem, newV, vListAdapter, vIDs, openList, subItems);
            }
        });
    }

    /**
     * updates the status of the vehicle when the user clicks on it
     * will either be open -> closed, closed -> open
     *
     * @param selectedItem the selected item/vehicle which will have its status updated
     * @param newV new status to be set for the vehicle
     * @param vListAdapter adapter managing the data displayed
     * @param vIDs IDs of the vehicles
     * @param openList arraylist that keeps track of the status (open or closed) of each vehicle
     * @param subItems holds details for the vehicles (seats left)
     */
    private void updateVehicleStatus(String selectedItem, boolean newV, ArrayAdapter<String> vListAdapter, ArrayList<String> vIDs, ArrayList<Boolean> openList, ArrayList<String> subItems) {
        firestore.collection("vehicle-collection")
                .document(selectedItem)
                .update("open", newV)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(MainActivity.this, "Status changed.", Toast.LENGTH_SHORT).show();
                        firestore.collection("vehicle-collection")
                                .document(selectedItem)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot d) {
                                        int pos = vIDs.indexOf(selectedItem);
                                        openList.set(pos, newV);
                                        String text = newV ? "Open - " : "Closed - ";
                                        subItems.set(pos, text + "Seat(s) left: " + String.valueOf(d.getLong("seatsLeft")));
                                        vListAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Failed to retrieve updated data. Try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Status could not be changed. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}