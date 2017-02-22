package com.gmail.dleemcewen.tandemfieri;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.Repositories.Restaurants;
import com.gmail.dleemcewen.tandemfieri.Repositories.Users;

public class AssignRestaurantDrivers extends AppCompatActivity {
    private Context context;
    private Users<User> usersRepository;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_restaurant_drivers);

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
        usersRepository = new Users<>(this);
        restaurantId = getIntent().getStringExtra("restaurantId");
    }

    /**
     * find all control references
     */
    private void findControlReferences() {
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {

    }

    /**
     * retrieve data
     */
    private void retrieveData() {

    }

    /**
     * perform any final layout updates
     */
    private void finalizeLayout() {

    }
}
