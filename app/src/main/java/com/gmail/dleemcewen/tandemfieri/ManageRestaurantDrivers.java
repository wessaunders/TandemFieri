package com.gmail.dleemcewen.tandemfieri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.dleemcewen.tandemfieri.Adapters.ManageRestaurantDriversListAdapter;
import com.gmail.dleemcewen.tandemfieri.Adapters.ManageRestaurantExpandableListAdapter;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.gmail.dleemcewen.tandemfieri.Logging.LogWriter;
import com.gmail.dleemcewen.tandemfieri.Repositories.Restaurants;
import com.gmail.dleemcewen.tandemfieri.Repositories.Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class ManageRestaurantDrivers extends AppCompatActivity {
    private Context context;
    private Resources resources;
    private static final int ASSIGN_DRIVERS = 1;
    private TextView restaurantName;
    private TextView driversCurrentlyAssignedToRestaurant;
    private Button addDrivers;
    private ListView restaurantDriverList;
    private ManageRestaurantDriversListAdapter listAdapter;
    private Users<User> users;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_restaurant_drivers);

        initialize();
        findControlReferences();
        bindEventHandlers();
        retrieveData();
        finalizeLayout();
    }

    /**
     * initialize all necessary variables
     */
    private void initialize() {
        context = this;
        resources = this.getResources();
        users = new Users<>(context);

        Bundle bundle = this.getIntent().getExtras();
        restaurant = (Restaurant)bundle.getSerializable("Restaurant");

        LogWriter.log(getApplicationContext(),
            Level.FINE,
            "Managing restaurant " + restaurant.getName() + "(" + restaurant.getKey() + ")");
    }

    /**
     * find all control references
     */
    private void findControlReferences() {
        restaurantName = (TextView)findViewById(R.id.restaurantName);
        driversCurrentlyAssignedToRestaurant = (TextView)findViewById(R.id.driversCurrentlyAssignedToRestaurant);
        addDrivers = (Button)findViewById(R.id.addDrivers);
        restaurantDriverList = (ListView)findViewById(R.id.restaurantDriverList);
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {
        addDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageRestaurantDrivers.this, AssignRestaurantDrivers.class);
                intent.putExtra("restaurantId", restaurant.getKey());
                startActivityForResult(intent, ASSIGN_DRIVERS);
            }
        });
    }

    /**
     * retrieve data
     */
    private void retrieveData() {
        //find all the users where the restaurantid matches the current restaurant id
        users.find(
            Arrays.asList("Driver", "restaurantId"),
            restaurant.getKey(),
            new QueryCompleteListener<User>() {
                @Override
                public void onQueryComplete(ArrayList<User> entities) {
                    listAdapter = new ManageRestaurantDriversListAdapter((Activity)context, entities);
                    restaurantDriverList.setAdapter(listAdapter);
                }
            });
    }

    /**
     * perform any final layout updates
     */
    private void finalizeLayout() {
        //set restaurant name value
        restaurantName.setText(restaurant.getName());

        String currentlyAssignedToRestaurant = resources.getString(R.string.driversCurrentlyAssigned);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(currentlyAssignedToRestaurant);
        stringBuilder.append(" ");
        stringBuilder.append(restaurant.getName());

        driversCurrentlyAssignedToRestaurant.setText(stringBuilder.toString());
        underlineText(driversCurrentlyAssignedToRestaurant);
    }

    /**
     * underline the text in the provided textview
     * @param textViewControl identifies the textview control containing the text to be underlined
     */
    private void underlineText(TextView textViewControl) {
        String textToUnderline = textViewControl.getText().toString();
        SpannableString content = new SpannableString(textToUnderline);
        content.setSpan(new UnderlineSpan(), 0, textToUnderline.length(), 0);
        textViewControl.setText(content);
    }
}
