package com.gmail.dleemcewen.tandemfieri;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.gmail.dleemcewen.tandemfieri.Adapters.DriverRatingsListAdapter;
import com.gmail.dleemcewen.tandemfieri.Comparators.RatingsByDriverInAscendingOrderComparator;
import com.gmail.dleemcewen.tandemfieri.Entities.Rating;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.gmail.dleemcewen.tandemfieri.Repositories.Ratings;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DriverRatings extends AppCompatActivity {
    private EditText startDate;
    private EditText endDate;
    private Switch activeInactiveDrivers;
    private Button viewDriverRatings;
    private ListView ratingsList;
    private Ratings<Rating> ratingsRepository;
    private Restaurant restaurant;
    private Calendar calendar;
    private boolean includeInactiveDrivers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ratings);

        initialize();
        findControlReferences();
        bindEventHandlers();
    }

    /**
     * initialize all necessary variables
     */
    private void initialize() {
        ratingsRepository = new Ratings<>();
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        restaurant = (Restaurant)bundle.getSerializable("Restaurant");
    }

    /**
     * find all control references
     */
    private void findControlReferences() {
        startDate = (EditText)findViewById(R.id.startDate);
        endDate = (EditText)findViewById(R.id.endDate);
        activeInactiveDrivers = (Switch)findViewById(R.id.activeInactiveDrivers);
        viewDriverRatings = (Button)findViewById(R.id.viewDriverRatings);
        ratingsList = (ListView)findViewById(R.id.driverRatingsList);
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(DriverRatings.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate.setText(
                                String.valueOf(month + 1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(DriverRatings.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate.setText(
                                String.valueOf(month + 1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }
        });

        activeInactiveDrivers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                includeInactiveDrivers = isChecked;
            }
        });

        viewDriverRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData();
            }
        });
    }

    /**
     * retrieve data
     */
    private void retrieveData() {
        final List<Rating> matchedRatingsList = new ArrayList<>();

        if (startDate.getText().toString().equals("") && endDate.getText().toString().equals("")) {
            //no start and end date range has been provided, so get all of the ratings for the current restaurant
            ratingsRepository.find(Arrays.asList("restaurantId"), Arrays.asList(restaurant.getKey()), new QueryCompleteListener<Rating>() {
                @Override
                public void onQueryComplete(ArrayList<Rating> entities) {
                    matchedRatingsList.addAll(entities);
                }
            });
        } else {
            //get all the ratings that are in the start and end date range
            ratingsRepository.find(
                Arrays.asList("date"),
                Arrays.asList(startDate.getText().toString(), endDate.getText().toString()),
                new QueryCompleteListener<Rating>() {
                    @Override
                    public void onQueryComplete(ArrayList<Rating> entities) {
                        //all ratings for date range could include ratings for other restaurants
                        //so filter out all entries except those for the current restaurant
                        for (Rating rating : entities) {
                            if (rating.getRestaurantId().equals(restaurant.getKey())) {
                                matchedRatingsList.add(rating);
                            }
                        }
                    }
            });
        }

        //arraylist to store driver and average rating
        ArrayList<AbstractMap.SimpleEntry<String, Double>> driverRatingsList = new ArrayList<>();

        //Sort ratingslist by driver in ascending order
        Collections.sort(matchedRatingsList, new RatingsByDriverInAscendingOrderComparator());

        //Ensure the method is working with a distinct list of drivers
        List<AbstractMap.SimpleEntry<String, String>> distinctDrivers =
                buildDistinctListOfDrivers(restaurant.getDrivers());

        //Calculate each driver's average rating
        for (AbstractMap.SimpleEntry<String, String> driver : distinctDrivers) {
            double averageRating = buildDriverAverageRating(driver.getKey(), matchedRatingsList);
            AbstractMap.SimpleEntry<String, Double> driverRating =
                new AbstractMap.SimpleEntry<>(driver.getValue(), averageRating);
            driverRatingsList.add(driverRating);
        }

        DriverRatingsListAdapter adapter = new DriverRatingsListAdapter(this, driverRatingsList);
        ratingsList.setAdapter(adapter);
    }

    /**
     * buildDistinctListOfDrivers builds a distinct list of drivers for the current restaurant
     * @param drivers indicates the drivers associated with the current restaurant
     * @return a distinct list of the driver ids and names
     */
    private List<AbstractMap.SimpleEntry<String, String>> buildDistinctListOfDrivers(List<User> drivers) {
        List<AbstractMap.SimpleEntry<String, String>> distinctListOfDrivers = new ArrayList<>();

        if (drivers != null && !drivers.isEmpty()) {
            Set<String> distinctDriverIds =  buildDistinctListOfDriverIds();
            for (String driverId : distinctDriverIds) {
                AbstractMap.SimpleEntry<String, String> distinctDriver = null;
                for (User driver : drivers) {
                    if (distinctDriver == null && driver.getAuthUserID().equals(driverId)) {
                        StringBuilder driverNameBuilder = new StringBuilder();
                        driverNameBuilder.append(driver.getFirstName());
                        driverNameBuilder.append(" ");
                        driverNameBuilder.append(driver.getLastName());

                        distinctDriver = new AbstractMap.SimpleEntry<>(driverId, driverNameBuilder.toString());
                    }
                }

                distinctListOfDrivers.add(distinctDriver);
            }
        }

        return distinctListOfDrivers;
    }

    /**
     * buildDistinctListOfDriverIds returns a list of distinct driver ids
     * @return list of distinct driver ids
     */
    private Set<String> buildDistinctListOfDriverIds() {
        Set<String> distinctDriverIds = new HashSet<String>();
        for (User driver : restaurant.getDrivers()) {
            if (driver.getActive() || (!driver.getActive() && includeInactiveDrivers)) {
                distinctDriverIds.add(driver.getAuthUserID());
            }
        }
        return distinctDriverIds;
    }

    /**
     * buildDriverAverageRating builds the average rating for the provided driverid
     * @param driverId uniquely identifies the driver to build the average rating for
     * @param matchedRatingsList indicates the list of ratings that match the query criteria
     * @return average rating for the provided driverid
     */
    private double buildDriverAverageRating(String driverId, List<Rating> matchedRatingsList) {
        double totalRating = 0;
        int numberOfRatings = 0;

        for (Rating rating : matchedRatingsList) {
            if (rating.getDriverId().equals(driverId)) {
                totalRating += rating.getRating();
                numberOfRatings++;
            }
        }

        return totalRating/numberOfRatings;
    }
}
