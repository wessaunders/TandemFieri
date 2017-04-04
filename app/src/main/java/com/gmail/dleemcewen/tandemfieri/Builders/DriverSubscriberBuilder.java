package com.gmail.dleemcewen.tandemfieri.Builders;

import android.content.Context;

import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.Enums.OrderEnum;
import com.gmail.dleemcewen.tandemfieri.EventListeners.SubscriberBuilderCompleteListener;
import com.gmail.dleemcewen.tandemfieri.Filters.SubscriberFilter;
import com.gmail.dleemcewen.tandemfieri.Interfaces.ISubscriber;
import com.gmail.dleemcewen.tandemfieri.Subscribers.DriverSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * DriverSubscriberBuilder abstracts the logic used to build a new driver subscriber that can be registered
 * with a NotificationPublisher
 */

public class DriverSubscriberBuilder {
    public static void Build(final User driver,
                             final Context context,
                             final SubscriberBuilderCompleteListener<DriverSubscriber> subscriberBuilderCompleteListener) {

        List<ISubscriber> subscribers = new ArrayList<>();

        List<Object> restaurantIds = new ArrayList<>();
        restaurantIds.add(driver.getRestaurantId());

        List<Object> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderEnum.EN_ROUTE.toString());

        List<SubscriberFilter> subscriberFilters = new ArrayList<>();
        subscriberFilters.add(new SubscriberFilter("restaurantId", restaurantIds));
        subscriberFilters.add(new SubscriberFilter("status", orderStatuses));

        ISubscriber enrouteSubscriber = new DriverSubscriber(
                context,
                driver,
                subscriberFilters);

        orderStatuses.clear();
        orderStatuses.add(OrderEnum.REFUNDED.toString());

        ISubscriber refundedSubscriber = new DriverSubscriber(
                context,
                driver,
                subscriberFilters);

        subscribers.add(enrouteSubscriber);
        subscribers.add(refundedSubscriber);

        subscriberBuilderCompleteListener.onBuildComplete(subscribers);
    }
}
