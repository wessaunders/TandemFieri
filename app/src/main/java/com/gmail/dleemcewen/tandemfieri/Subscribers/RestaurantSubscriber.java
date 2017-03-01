package com.gmail.dleemcewen.tandemfieri.Subscribers;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Interfaces.ISubscriber;
import com.gmail.dleemcewen.tandemfieri.Logging.LogWriter;
import com.gmail.dleemcewen.tandemfieri.R;

import java.util.Map;
import java.util.logging.Level;

import static android.R.attr.action;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * RestaurantSubscriber identifies a restaurant subscriber
 */

public class RestaurantSubscriber implements ISubscriber {
    private Context context;
    private String userId;
    private static final String notificationType = "Restaurant";

    /**
     * Default constructor
     * @param context indicates the current application context
     * @param userId uniquely identifies the user
     */
    public RestaurantSubscriber(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    @Override
    public String getNotificationType() {
        return notificationType;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void update(Bundle notification) {
        if (context.getResources().getBoolean(R.bool.send_notifications)) {
            //TODO: update this to use orders instead of restaurants
            //Since the orders are not yet available, this simply simulates receiving a notification
            Restaurant restaurant = (Restaurant)notification.getSerializable("entity");

            StringBuilder notificationTextBuilder = new StringBuilder();
            notificationTextBuilder.append(restaurant.getName());
            notificationTextBuilder.append(" received ");
            notificationTextBuilder.append(notification.getString("action") == "ADDED" ? "a new " : "an updated ");
            notificationTextBuilder.append(" order!");

            // Send Notification
            NotificationCompat.Builder notificationBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.cast_ic_notification_0)
                            .setContentTitle(notificationType + " Notification")
                            .setContentText(notificationTextBuilder.toString());

            // Sets an ID for the notification
            int notificationId = 1;

            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            // Builds the notification and issues it.
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }
}
