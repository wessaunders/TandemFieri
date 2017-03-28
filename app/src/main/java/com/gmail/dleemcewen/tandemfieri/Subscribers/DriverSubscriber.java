package com.gmail.dleemcewen.tandemfieri.Subscribers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.gmail.dleemcewen.tandemfieri.DriverMainMenu;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.Filters.SubscriberFilter;
import com.gmail.dleemcewen.tandemfieri.Interfaces.ISubscriber;
import com.gmail.dleemcewen.tandemfieri.R;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * DriverSubscriber identifies a driver that has subscribed to notification messages
 */

public class DriverSubscriber implements ISubscriber {
    private Context context;
    private List<SubscriberFilter> filters;
    private User driverUser;
    private static final String notificationType = "Order";

    /**
     * Default constructor
     * @param context indicates the current application context
     * @param driverUser identifies the driver user
     * @param filters indicates the record filters supplied by the subscriber
     */
    public DriverSubscriber(Context context, User driverUser, List<SubscriberFilter> filters) {
        this.context = context;
        this.driverUser = driverUser;
        this.filters = filters;
    }

    /**
     * Optional constructor
     * @param context indicates the current application context
     */
    public DriverSubscriber(Context context) {
        this.context = context;
        this.filters = null;
    }

    /**
     * getNotificationType returns the type of notifications the subscriber wishes to receive
     *
     * @return notification type
     */
    @Override
    public String getNotificationType() {
        return notificationType;
    }

    /**
     * getFilters returns the record filters supplied by the subscriber
     *
     * @return record filters
     */
    @Override
    public List<SubscriberFilter> getFilters() {
        return filters;
    }

    /**
     * update provides the subscriber with updated information
     *
     * @param notification indicates the notification
     */
    @Override
    public void update(Bundle notification) {
        if (context.getResources().getBoolean(R.bool.send_notifications)) {
            HashMap notificationData = ((HashMap) notification.getSerializable("entity"));

            HashMap orderDateFields = (HashMap)notificationData.get("orderDate");
            StringBuilder dateString = new StringBuilder();
            dateString.append(Integer.valueOf(orderDateFields.get("month").toString()) + 1);
            dateString.append("/");
            dateString.append(Integer.valueOf(orderDateFields.get("date").toString()));
            dateString.append("/");
            dateString.append(Integer.valueOf(orderDateFields.get("year").toString()) + 1900);

            StringBuilder contentTextBuilder = new StringBuilder();
            contentTextBuilder.append("Delivery ready for ");
            contentTextBuilder.append(notificationData.get("restaurantName").toString());

            StringBuilder notificationTextBuilder = new StringBuilder();
            notificationTextBuilder.append("You have a new delivery order from ");
            notificationTextBuilder.append(notificationData.get("restaurantName").toString());
            notificationTextBuilder.append(" containing ");
            notificationTextBuilder.append(((List)notificationData.get("items")).size());
            notificationTextBuilder.append(" items. The order was placed on ");
            notificationTextBuilder.append(dateString.toString());
            notificationTextBuilder.append(".");

            //set an id for the notification
            int notificationId = Integer.valueOf(notificationData.get("notificationId").toString());

            Bundle viewDeliveryBundle = new Bundle();
            viewDeliveryBundle.putSerializable("User", driverUser);
            viewDeliveryBundle.putInt("notificationId", notificationId);

            Intent viewDeliveryIntent = new Intent(context, DriverMainMenu.class);
            viewDeliveryIntent.putExtras(viewDeliveryBundle);
            viewDeliveryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            TaskStackBuilder viewDeliveryBackStackBuilder = TaskStackBuilder.create(context);
            viewDeliveryBackStackBuilder.addNextIntentWithParentStack(viewDeliveryIntent);

            PendingIntent viewDeliveryPendingIntent = viewDeliveryBackStackBuilder
                    .getPendingIntent(1, PendingIntent.FLAG_CANCEL_CURRENT);

            // Build notification
            NotificationCompat.Builder notificationBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_mail_outline)
                            .setContentTitle(notificationType + " notification message")
                            .setContentText(contentTextBuilder.toString())
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationTextBuilder.toString()))
                            .setAutoCancel(false)
                            .addAction(R.drawable.ic_open_in_new, "View delivery", viewDeliveryPendingIntent);

            // This sets the pending intent that should be fired when the user clicks the
            // notification. Clicking the notification launches a new activity.
            notificationBuilder
                    .setContentIntent(viewDeliveryPendingIntent);

            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            // Builds the notification and issues it.
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }
}
