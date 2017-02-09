package com.gmail.dleemcewen.tandemfieri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Repositories.Restaurants;
import com.gmail.dleemcewen.tandemfieri.Validator.Validator;

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
    private Button deliveryArea;
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
        restaurantsRepository = new Restaurants<>();
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
        deliveryArea = (Button)findViewById(R.id.deliveryArea);
        createRestaurant = (Button)findViewById(R.id.createRestaurant);
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {
        createRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validator.isValid(restaurantName, getString(R.string.nameRequired))
                    && Validator.isValid(street, getString(R.string.streetRequired))
                    && Validator.isValid(city, getString(R.string.cityRequired))
                    && Validator.isValid(state, getString(R.string.stateRequired))
                    && Validator.isValid(zipCode, getString(R.string.zipRequired))) {

                    //build a new restaurant
                    Restaurant restaurant = buildNewRestaurant();

                    //save the restaurant record
                    restaurantsRepository.add(restaurant);
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

        return restaurant;
    }
}
