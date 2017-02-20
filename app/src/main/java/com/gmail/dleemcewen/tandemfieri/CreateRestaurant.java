package com.gmail.dleemcewen.tandemfieri;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Events.ActivityEvent;
import com.gmail.dleemcewen.tandemfieri.Repositories.Restaurants;
import com.gmail.dleemcewen.tandemfieri.Validator.Validator;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseError;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

public class CreateRestaurant extends AppCompatActivity {
    private Restaurants<Restaurant> restaurantsRepository;
    private TextView title;
    private TextView address;
    private TextView delivery;
    private EditText restaurantName;
    private EditText street;
    private EditText city;
    private EditText state;
    private EditText zipCode;
    private EditText deliveryCharge;
    private Button businessHours;
    private Button createRestaurant;
    private String restaurantOwnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);

        initialize();
        findControlReferences();
        bindEventHandlers();
        finalizeLayout();
    }

    /**
     * initialize all necessary variables
     */
    private void initialize() {
        restaurantsRepository = new Restaurants<>(this);
        restaurantOwnerId = getIntent().getStringExtra("ownerId");
    }

    /**
     * find all control references
     */
    private void findControlReferences() {
        title = (TextView)findViewById(R.id.title);
        address = (TextView)findViewById(R.id.address);
        delivery = (TextView)findViewById(R.id.delivery);
        restaurantName = (EditText)findViewById(R.id.restaurantName);
        street = (EditText)findViewById(R.id.street);
        city = (EditText)findViewById(R.id.city);
        state = (EditText)findViewById(R.id.state);
        zipCode = (EditText)findViewById(R.id.zipcode);
        deliveryCharge = (EditText)findViewById(R.id.deliveryCharge);
        businessHours = (Button)findViewById(R.id.businessHours);
        createRestaurant = (Button)findViewById(R.id.createRestaurant);
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {
        businessHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateRestaurant.this, "Opening soon!", Toast.LENGTH_SHORT).show();
            }
        });

        createRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForValidData()) {
                    //build a new restaurant
                    Restaurant restaurant = buildNewRestaurant();

                    //add the restaurant record
                    //and then check the return value to ensure the restaurant was created successfully
                    restaurantsRepository
                        .add(restaurant)
                        .continueWith(new Continuation<Map.Entry<Boolean ,DatabaseError>,
                                Task<Map.Entry<Boolean, DatabaseError>>>() {
                            @Override
                            public Task<Map.Entry<Boolean, DatabaseError>> then(@NonNull Task<Map.Entry<Boolean, DatabaseError>> task)
                                    throws Exception {
                                TaskCompletionSource<Map.Entry<Boolean, DatabaseError>> taskCompletionSource =
                                    new TaskCompletionSource<>();

                                Map.Entry<Boolean, DatabaseError> taskResult = task.getResult();
                                StringBuilder toastMessage = new StringBuilder();

                                if (taskResult.getKey()) {
                                    toastMessage.append("Restaurant created successfully");
                                } else {
                                    toastMessage.append("An error occurred while creating the restaurant.  Please check your network connection and try again.");
                                }
                                Toast
                                    .makeText(CreateRestaurant.this, toastMessage.toString(), Toast.LENGTH_LONG)
                                    .show();

                                //Only go back to the manage restaurants screen if the restaurant was created successfully...
                                if (taskResult.getKey()) {
                                    EventBus.getDefault().post(new ActivityEvent(ActivityEvent.Result.REFRESH_RESTAURANT_LIST));
                                    finish();
                                }

                                return taskCompletionSource.getTask();
                            }
                        });
                }
            }
        });
    }

    /**
     * perform any final layout updates
     */
    private void finalizeLayout() {
        underlineText(title);
        underlineText(address);
        underlineText(delivery);
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

    /**
     * build a new restaurant entity
     * @return new restaurant entity
     */
    private Restaurant buildNewRestaurant() {
        //Create new restaurant entity
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantName.getText().toString());
        restaurant.setStreet(street.getText().toString());
        restaurant.setCity(city.getText().toString());
        restaurant.setState(state.getText().toString());
        restaurant.setZipcode(zipCode.getText().toString());
        restaurant.setCharge(Double.valueOf(deliveryCharge.getText().toString()));
        restaurant.setOwnerId(restaurantOwnerId);
        restaurant.setDeliveryRadius(getBaseContext().getResources().getInteger(R.integer.defaultDeliveryRadius));

        return restaurant;
    }

    /**
     * checkForValidData checks to ensure that the information entered in to the create restaurant
     * view is valid
     * @return true or false
     */
    private boolean checkForValidData() {
        ArrayList<Boolean> validations = new ArrayList<>();

        validations.add(Validator.isValid(restaurantName, getString(R.string.nameRequired)));
        validations.add(Validator.isValid(street, getString(R.string.streetRequired)));
        validations.add(Validator.isValid(city, getString(R.string.cityRequired)));
        validations.add(Validator.isValid(state, getString(R.string.stateRequired)));
        validations.add(Validator.isValid(zipCode, FormConstants.REG_EX_ZIP, FormConstants.ERROR_TAG_ZIP));
        validations.add(Validator.isValid(deliveryCharge, getString(R.string.deliveryChargeRequired)));
        validations.add(Validator.isValid(deliveryCharge, FormConstants.REG_EX_MONETARY,
                getString(R.string.deliveryChargeGreaterThanZero)));

        return !validations.toString().contains("false");
    }
}