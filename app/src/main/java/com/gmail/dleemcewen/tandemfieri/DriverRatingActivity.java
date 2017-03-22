package com.gmail.dleemcewen.tandemfieri;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gmail.dleemcewen.tandemfieri.Entities.NotificationMessage;
import com.gmail.dleemcewen.tandemfieri.Entities.Order;
import com.gmail.dleemcewen.tandemfieri.Entities.Rating;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.Formatters.DateFormatter;
import com.gmail.dleemcewen.tandemfieri.Repositories.NotificationMessages;
import com.gmail.dleemcewen.tandemfieri.Repositories.Ratings;
import com.gmail.dleemcewen.tandemfieri.Tasks.TaskResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DriverRatingActivity extends AppCompatActivity {
    private NotificationMessages<NotificationMessage> notificationsRepository;
    private Ratings<Rating> ratingsRepository;
    private RatingBar driverRatingBar;
    private TextView driverRatingText;
    private Button cancelDriverRating;
    private Button saveDriverRating;
    private int notificationId;
    private User dinerUser;
    private HashMap orderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_driver_rating);

        notificationsRepository = new NotificationMessages<>(DriverRatingActivity.this);
        ratingsRepository = new Ratings<>(DriverRatingActivity.this);

        initialize();
        findControlReferences();
        bindEventHandlers();
        retrieveData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (notificationsRepository == null) {
            notificationsRepository = new NotificationMessages<>(DriverRatingActivity.this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        notificationsRepository.finalize();
        notificationsRepository = null;
    }

    /**
     * initialize all necessary variables
     */
    private void initialize() {
        this.setFinishOnTouchOutside(false);
        Bundle bundle = this.getIntent().getExtras();
        notificationId = bundle.getInt("notificationId");
        dinerUser = (User)bundle.getSerializable("User");
    }

    /**
     * find all control references
     */
    private void findControlReferences() {
        driverRatingBar = (RatingBar)findViewById(R.id.driverRatingBar);
        driverRatingText = (TextView)findViewById(R.id.driverRatingText);
        //cancelDriverRating = (Button)findViewById(R.id.cancelDriverRating);
        //saveDriverRating = (Button)findViewById(R.id.saveDriverRating);
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {
        driverRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                driverRatingText.setText(String.valueOf(rating));
            }
        });

        /*cancelDriverRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveDriverRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rating newDriverRating = new Rating();
                newDriverRating.setDate(DateFormatter.toTimeStamp(new Date()).toString());
                newDriverRating.setRating(driverRatingBar.getRating());
                newDriverRating.setRestaurantId(orderData.get("restaurantId").toString());
                newDriverRating.setOrderId(orderData.get("orderId").toString());
                newDriverRating.setDriverId(dinerUser.getAuthUserID());

                ratingsRepository.add(newDriverRating);

                finish();
            }
        });*/
    }

    /**
     * retrieve data
     */
    private void retrieveData() {
        if (notificationId != 0) {
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.cancel(notificationId);

            notificationsRepository
                    .find("notificationId = '" + notificationId + "'")
                    .addOnCompleteListener(DriverRatingActivity.this, new OnCompleteListener<TaskResult<NotificationMessage>>() {
                        @Override
                        public void onComplete(@NonNull Task<TaskResult<NotificationMessage>> task) {
                            List<NotificationMessage> messages = task.getResult().getResults();
                            if (!messages.isEmpty()) {
                                orderData = (HashMap)messages.get(0).getData();
                                notificationsRepository.remove(messages.get(0));
                            }
                        }
                    });
        }
    }



    //Build alert dialog with custom view
    /*AlertDialog.Builder rateRestaurantDialog  = new AlertDialog.Builder(context);
    rateRestaurantDialog.setView(dialogLayout);
    rateRestaurantDialog
            .setTitle(dialogTitleBuilder.toString());
    rateRestaurantDialog.setCancelable(false);
    rateRestaurantDialog.setPositiveButton(
            resources.getString(R.string.save),
            new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            Rating newRestaurantRating = new Rating();
            newRestaurantRating.setDate(DateFormatter.toTimeStamp(new Date()).toString());
            newRestaurantRating.setRating(restaurantRatingBar.getRating());
            newRestaurantRating.setRestaurantId(restaurant.getId());

            ratingsRepository.add(newRestaurantRating);

            dialog.cancel();
        }
    });
    rateRestaurantDialog.setNegativeButton(
            resources.getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    });


    Button close_button = (Button) findViewById(R.id.close_button);
close_button.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
    }
});
    */
}
