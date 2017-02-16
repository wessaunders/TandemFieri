package com.gmail.dleemcewen.tandemfieri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.gmail.dleemcewen.tandemfieri.Adapters.ManageRestaurantExpandableListAdapter;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.gmail.dleemcewen.tandemfieri.MenuBuilder.MenuCatagory;
import com.gmail.dleemcewen.tandemfieri.Events.ActivityEvent;
import com.gmail.dleemcewen.tandemfieri.Repositories.Restaurants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageRestaurants extends AppCompatActivity {
    private TextView header;
    private Button addRestaurant;
    private ExpandableListView restaurantsList;
    private ManageRestaurantExpandableListAdapter listAdapter;
    private Restaurants<Restaurant> restaurants;
    private User currentUser;
    private Context context;
    private static final int CREATE_RESTAURANT = 1;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_RESTAURANT && resultCode == RESULT_OK) {
            //A new restaurant was added, so reload the restaurants data
            retrieveData();
        }
        if (requestCode == 111 && resultCode == RESULT_OK) {
            //update main menu
            MenuCatagory temp = (MenuCatagory) data.getSerializableExtra("now");
            Restaurant restaurant = (Restaurant) data.getSerializableExtra("resturaunt");
            restaurant = listAdapter.findRefrenceToUpdate(restaurant);
            restaurant.setMenu(temp);

            Restaurants<Restaurant> restaurantsRepository = new Restaurants<>();

            restaurantsRepository
                    .update(restaurant);
//                    .continueWith(new Continuation<AbstractMap.SimpleEntry<Boolean ,DatabaseError>,
//                            Task<AbstractMap.SimpleEntry<Boolean, DatabaseError>>>() {
//                        @Override
//                        public Task<AbstractMap.SimpleEntry<Boolean, DatabaseError>> then(@NonNull Task<AbstractMap.SimpleEntry<Boolean, DatabaseError>> task)
//                                throws Exception {
//                            TaskCompletionSource<AbstractMap.SimpleEntry<Boolean, DatabaseError>> taskCompletionSource =
//                                    new TaskCompletionSource<>();
//
//                            AbstractMap.SimpleEntry<Boolean, DatabaseError> taskResult = task.getResult();
//                            StringBuilder toastMessage = new StringBuilder();
//
//                            if (taskResult.getKey()) {
//                                toastMessage.append("Restaurant updated successfully");
//                            } else {
//                                toastMessage.append(taskResult.getValue().getMessage());
//                                toastMessage.append(". The restaurant was not created correctly");
//                            }
//
//                            Toast
//                                    .makeText(ManageRestaurants.this, toastMessage.toString(), Toast.LENGTH_LONG)
//                                    .show();
//
//                            Intent intent=new Intent();
//                            setResult(RESULT_OK,intent);
//                            finish();
//
//                            return taskCompletionSource.getTask();
//                        }
//                    });
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * initialize all necessary variables
     */
    private void initialize() {
        context = this;
        restaurants = new Restaurants<>();

        Bundle bundle = this.getIntent().getExtras();
        currentUser = (User)bundle.getSerializable("User");

        EventBus.getDefault().register(this);
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
                startActivityForResult(intent, CREATE_RESTAURANT);
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
                    listAdapter = new ManageRestaurantExpandableListAdapter((Activity)context, entities, buildExpandableChildData(entities));
                    restaurantsList.setAdapter(listAdapter);
                }
        });
    }

    /**
     * buildExpandableChildData builds the data that is associated with the expandable child entries
     * @param entities indicates a list of restaurants returned by the retrieveData method
     * @return Map of the expandable child data
     */
    private Map<String, List<Restaurant>> buildExpandableChildData(List<Restaurant> entities) {
        HashMap<String, List<Restaurant>> childData = new HashMap<>();
        for (Restaurant entity : entities) {
            childData.put(entity.getKey(), Arrays.asList(entity));
        }

        return childData;
    }

    /**
     * perform any final layout updates
     */
    private void finalizeLayout() {
        //set header value
        header.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
    }

    @Subscribe
    public void onEvent(ActivityEvent event) {
        if (event.result == ActivityEvent.Result.REFRESH_RESTAURANT_LIST) {
            retrieveData();
        }
    }
}