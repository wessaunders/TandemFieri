package com.gmail.dleemcewen.tandemfieri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.dleemcewen.tandemfieri.Entities.Order;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.button;


public class ManageOrders extends AppCompatActivity {
    public DatabaseReference mDatabaseOrders, mDatabaseDrivers, mDatabaseSendToDrivers;
    public String ID, restID, clickedYes;
    public Order order;
    private ListView driverListView;
    private List<String> driverList;
    private List<User> logicList;
    private DatabaseReference mDatabase, mQuery;
    private User d, owner;
    private AlertDialog.Builder alertDialogBuilder;
    public int i;
    public User user;
    public boolean controlBool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);


        alertDialogBuilder = new AlertDialog.Builder(this);


        Bundle bundle = this.getIntent().getExtras();
        order = (Order) bundle.getSerializable("Order");
        owner = (User) bundle.getSerializable("Owner");


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mQuery = FirebaseDatabase.getInstance().getReference().child("Delivery");
        /*
        Some kind of on click listener will be used to send the order to driver for the driver to get it use
        mDatabase.child("Delivery").child(driverID).child("Order").setValue(order);

         */

        driverList = new ArrayList<>();
        logicList = new ArrayList<>();
        driverListView = (ListView) findViewById(R.id.driver_listview);


        driverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                user = (User) logicList.get(position);
                String temp = (String) parent.getItemAtPosition(position);
                String otherDriver;
                controlBool = false;

                for(i = 0; i < driverListView.getCount(); i++){
                    String temp2 = (String) parent.getItemAtPosition(i);
                    if(temp2.contains(" | Assigned To") && i != position){

                        controlBool = true;
                        User user2 = (User) logicList.get(i);
                        mDatabase.child("Delivery").child(user2.getAuthUserID()).child("Order").child(order.getCustomerId()).removeValue();
                        mDatabase.child("Delivery").child(user.getAuthUserID()).child("Order").child(order.getCustomerId()).setValue(order);

                    }
                }


                if(controlBool == false) {
                    if (temp.contains(" | Assigned To")) {
                        mDatabase.child("Delivery").child(user.getAuthUserID()).child("Order").child(order.getCustomerId()).removeValue();
                        mDatabase.child("Order").child(owner.getAuthUserID()).child(order.getOrderId()).child("assigned").removeValue();
                    } else {

                        Toast.makeText(getApplicationContext(), "Sent the order to " + user.getFirstName(), Toast.LENGTH_LONG).show();
                        mDatabase.child("Delivery").child(user.getAuthUserID()).child("Order").child(order.getCustomerId()).setValue(order);
                        mDatabase.child("Order").child(owner.getAuthUserID()).child(order.getOrderId()).child("assigned").setValue("True");
                    }
                }
                controlBool = false;
            }

        });

        // This is causing an error....
        // doesn't look like Order is ever added to the bundle
        order = (Order) bundle.getSerializable("Order");
        restID = order.getRestaurantId();

        mDatabaseDrivers = FirebaseDatabase.getInstance().getReference();
        mQuery = FirebaseDatabase.getInstance().getReference().child("Delivery");


        //TODO: need to remove valueeventlistener or use addListenerForSingleValueEvent
        //otherwise this will keep listening and toasting throughout the entire application
        //after ManageOrders is visited
        mDatabaseDrivers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverList.clear();
                logicList.clear();
                for (DataSnapshot child : dataSnapshot.child("User").child("Driver").getChildren()) {
                    if (restID.equals("" + child.child("restaurantId").getValue())) {
                        //Toast.makeText(getApplicationContext(), child.child("firstName").getValue().toString(), Toast.LENGTH_LONG).show();
                        User d = child.getValue(User.class);

                        if (dataSnapshot.child("Delivery").child(d.getAuthUserID()).child("Order").child(order.getCustomerId()).exists()) {
                            driverList.add(d.getFirstName() + " " + d.getLastName() + " | Assigned To");
                            logicList.add(d);
                        } else {
                            driverList.add(d.getFirstName() + " " + d.getLastName());
                            logicList.add(d);
                        }

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            R.layout.diner_mainmenu_item_view,
                            driverList);

                    driverListView.setAdapter(adapter);
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}