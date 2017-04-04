package com.gmail.dleemcewen.tandemfieri.Builders;


import android.content.Context;

import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.Enums.OrderEnum;
import com.gmail.dleemcewen.tandemfieri.EventListeners.SubscriberBuilderCompleteListener;
import com.gmail.dleemcewen.tandemfieri.Filters.SubscriberFilter;
import com.gmail.dleemcewen.tandemfieri.Interfaces.ISubscriber;
import com.gmail.dleemcewen.tandemfieri.Subscribers.DinerSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * DinerSubscriberBuilder abstracts the logic used to build a new diner subscriber that can be registered
 * with a NotificationPublisher
 */

public class DinerSubscriberBuilder {
    public static void Build(final User diner,
                             final Context context,
                             final SubscriberBuilderCompleteListener<DinerSubscriber> subscriberBuilderCompleteListener) {
        List<ISubscriber> subscribers = new ArrayList<>();

        List<Object> customerIds = new ArrayList<>();
        customerIds.add(diner.getAuthUserID());

        List<Object> orderStatuses = new ArrayList<>();
        orderStatuses.add(OrderEnum.REFUNDED.toString());

        List<SubscriberFilter> subscriberFilters = new ArrayList<>();
        subscriberFilters.add(new SubscriberFilter("customerId", customerIds));
        subscriberFilters.add(new SubscriberFilter("status", orderStatuses));

        ISubscriber refundSubscriber = new DinerSubscriber(
                context,
                diner,
                subscriberFilters);

        orderStatuses.clear();
        orderStatuses.add(OrderEnum.COMPLETE.toString());

        ISubscriber completedSubscriber = new DinerSubscriber(
                context,
                diner,
                subscriberFilters);

        subscribers.add(refundSubscriber);
        subscribers.add(completedSubscriber);

        subscriberBuilderCompleteListener.onBuildComplete(subscribers);
    }
}
