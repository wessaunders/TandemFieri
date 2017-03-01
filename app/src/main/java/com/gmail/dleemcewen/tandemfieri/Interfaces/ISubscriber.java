package com.gmail.dleemcewen.tandemfieri.Interfaces;

import android.os.Bundle;

/**
 * ISubscriber describes the interface used by all subscribers
 */

public interface ISubscriber {
    /**
     * getNotificationType returns the type of notifications the subscriber wishes to receive
     * @return notification type
     */
    String getNotificationType();

    /**
     * getUserId returns the user id of the subscriber that wishes to receive notifications
     * @return userid
     */
    String getUserId();

    /**
     * update provides the subscriber with updated information
     * @param notification indicates the notification
     */
    void update(Bundle notification);
}
