package com.gmail.dleemcewen.tandemfieri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.gmail.dleemcewen.tandemfieri.Adapters.ManageRestaurantExpandableListAdapter;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.gmail.dleemcewen.tandemfieri.Repositories.Restaurants;

import java.util.ArrayList;
import java.util.Arrays;

public class ManageRestaurants extends AppCompatActivity {
    TextView header;
    Button addRestaurant;
    ExpandableListView restaurantsList;
    ExpandableListAdapter listAdapter;
    Restaurants<Restaurant> restaurants;
    User currentUser;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_restaurants);

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
        restaurants = new Restaurants<>();

        Bundle bundle = this.getIntent().getExtras();
        currentUser = (User)bundle.getSerializable("User");
    }

    /**
     * find all control references
     */
    private void findControlReferences() {
        header = (TextView)findViewById(R.id.header);
        addRestaurant = (Button)findViewById(R.id.addRestaurant);
        restaurantsList = (ExpandableListView)findViewById(R.id.restaurantsList);
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {
        addRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageRestaurants.this, CreateRestaurant.class);
                intent.putExtra("ownerId", currentUser.getAuthUserID());
            }
        });
    }

    /**
     * retrieve data
     */
    private void retrieveData() {
        //find all the restaurants where the ownerid matches the current user id
        restaurants.find(
            Arrays.asList("ownerId"),
            currentUser.getAuthUserID(),
            new QueryCompleteListener<Restaurant>() {
                @Override
                public void onQueryComplete(ArrayList<Restaurant> entities) {
                    listAdapter = new ManageRestaurantExpandableListAdapter((Activity)context, entities);
                    restaurantsList.setAdapter(listAdapter);
                }
        });
    }

    /**
     * perform any final layout updates
     */
    private void finalizeLayout() {
        //set header value
        header.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
    }
}
