package com.gmail.dleemcewen.tandemfieri;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dleemcewen.tandemfieri.Adapters.RestaurantMainMenuExpandableListAdapter;
import com.gmail.dleemcewen.tandemfieri.Entities.NotificationMessage;
import com.gmail.dleemcewen.tandemfieri.Entities.Order;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.Enums.OrderEnum;
import com.gmail.dleemcewen.tandemfieri.Logging.LogWriter;
import com.gmail.dleemcewen.tandemfieri.Repositories.NotificationMessages;
import com.gmail.dleemcewen.tandemfieri.Tasks.TaskResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class RestaurantMainMenu extends AppCompatActivity  implements DatePickerFragment.DateListener{

    private User user;
    private NotificationMessages<NotificationMessage> notificationsRepository;
    private ExpandableListView orderList;
    private RestaurantMainMenuExpandableListAdapter listAdapter;
    private DatabaseReference mDatabase;
    private Context context;
    int id;
    private TextView fromDate;
    private TextView toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main_menu);

        context = this;

        notificationsRepository = new NotificationMessages<>(RestaurantMainMenu.this);

        Bundle bundle = this.getIntent().getExtras();
        user = (User) bundle.getSerializable("User");
        orderList = (ExpandableListView)findViewById(R.id.order_list);
        //header = (TextView) findViewById(header);

        int notificationId = bundle.getInt("notificationId");
        if (notificationId != 0) {
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.cancel(notificationId);

            notificationsRepository
                .find("notificationId = '" + notificationId + "'")
                .addOnCompleteListener(RestaurantMainMenu.this, new OnCompleteListener<TaskResult<NotificationMessage>>() {
                    @Override
                    public void onComplete(@NonNull Task<TaskResult<NotificationMessage>> task) {
                        List<NotificationMessage> messages = task.getResult().getResults();
                        if (!messages.isEmpty()) {
                            notificationsRepository.remove(messages.get(0));
                        }
                    }
                });
        }//end notification block

        retrieveData();

        LogWriter.log(getApplicationContext(), Level.INFO, "The user is " + user.getEmail());
    }//end onCreate

    @Override
    protected void onStart() {
        super.onStart();
        if (notificationsRepository == null) {
            notificationsRepository = new NotificationMessages<>(RestaurantMainMenu.this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        notificationsRepository.finalize();
        notificationsRepository = null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_owner_menu, menu);
        return true;
    }

    //determine which menu option was selected and call that option's action method
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                signOut();
                return true;
            case R.id.edit_personal_info:
                editPersonalInformation();
                return true;
            case R.id.edit_password:
                editPassword();
                return true;
            case R.id.manage_restaurants:
                goToManageRestaurants();
                return true;
            case R.id.product_history:
                displayProductHistoryDateDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //called when user selects sign out from the drop down menu
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit me", true);
        startActivity(intent);
        finish();
    }

    //called when user selects edit information from the drop down menu
    private  void editPersonalInformation(){
        //need to send user type so that the user can be located in the database
        Bundle bundle = new Bundle();
        Intent intent = new Intent(RestaurantMainMenu.this, EditAccountActivity.class);
        bundle.putSerializable("User", user);
        intent.putExtras(bundle);
        intent.putExtra("UserType", "Restaurant");
        startActivity(intent);
    }

    //called when user selects edit password from the drop down menu
    private void editPassword(){
        //need to send user type so that the user can be located in the database
        Bundle bundle = new Bundle();
        Intent intent = new Intent(RestaurantMainMenu.this, EditPasswordActivity.class);
        bundle.putSerializable("User", user);
        intent.putExtras(bundle);
        intent.putExtra("UserType", "Restaurant");
        startActivity(intent);
    }

    //called when user selects manage restaurants from the drop down menu
    //for now this goes to main menu until I get the name of the activity - look on git hub?
    private void goToManageRestaurants(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(RestaurantMainMenu.this, ManageRestaurants.class);
        bundle.putSerializable("User", user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /***********************UPDATE LIST WITH ORDERS********************************/
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == CREATE_RESTAURANT || requestCode == UPDATE_RESTAURANT)
                && resultCode == RESULT_OK) {
            //A new restaurant was added or a restaurant was updated
            retrieveData();
        }
    }*/
/********** CALL DATABASE TO COLLECT ORDERS***************************/
    private void retrieveData() {
        //find all the orders where the restaurantid matches the current user id
        //Order table: userID -> order# -> order entity

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Order").child(user.getAuthUserID());
        mDatabase.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot orderSnapshot) {
                        List<Order> orderEntities = new ArrayList<Order>();
                        //Toast.makeText(context, "this is the list the user id pulls up: ", Toast.LENGTH_LONG).show();
                        for(DataSnapshot number: orderSnapshot.getChildren()){
                            //Toast.makeText(context, "outer loop: " + number.getKey(), Toast.LENGTH_LONG).show();//this gives me the restaurant id
                            for(DataSnapshot orders : number.getChildren()){
                                Order order = orders.getValue(Order.class);
                                //add the children to the adapter list
                                if(!order.getStatus().equals(OrderEnum.COMPLETE)) {
                                    orderEntities.add(order);
                                    //Toast.makeText((Activity)context, "innner loop: " + order.getCustomerId(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(orderEntities.isEmpty()){
                                Toast.makeText(getApplicationContext(), "There are no orders on file.", Toast.LENGTH_LONG).show();
                            }else {
                                listAdapter = new RestaurantMainMenuExpandableListAdapter(
                                        (Activity) context, orderEntities, buildExpandableChildData(orderEntities));
                                orderList.setAdapter(listAdapter);
                            }
                        }
                    }//end on data change

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                }
        );
    }

    /**
     * buildExpandableChildData builds the data that is associated with the expandable child entries
     * @param entities indicates a list of restaurants returned by the retrieveData method
     * @return Map of the expandable child data
     */
    private Map<String, List<Order>> buildExpandableChildData(List<Order> entities) {
        HashMap<String, List<Order>> childData = new HashMap<>();
        for (Order entity : entities) {
            childData.put(entity.getKey(), Arrays.asList(entity));
        }

        return childData;
    }

    @Override
    public void onFinishDialog(Date date) {
        SimpleDateFormat formatDateJava = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        if(id == fromDate.getId()){
            fromDate.setText(formatDateJava.format(date));
        }else if(id == toDate.getId()){
            toDate.setText(formatDateJava.format(date));
        }
    }

    public class DateListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            //begin date picker
            id = view.getId();
            DialogFragment fragment = new DatePickerFragment();
            fragment.show(getFragmentManager(), "datePicker");
        }
    }

    public void displayProductHistoryDateDialog(){
        LogWriter.log(getApplicationContext(), Level.INFO, "open dialog to get date range");
        // get date_dialog.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View dateDialogView = li.inflate(R.layout.date_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set date_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(dateDialogView);

        DateListener dateListener = new DateListener();

        fromDate = (TextView) dateDialogView.findViewById(R.id.from_date);
        toDate = (TextView) dateDialogView.findViewById(R.id.to_date);
        fromDate.setOnClickListener(dateListener);
        toDate.setOnClickListener(dateListener);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                //result.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}//end Activity
