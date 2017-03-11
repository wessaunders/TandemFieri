package com.gmail.dleemcewen.tandemfieri;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gmail.dleemcewen.tandemfieri.Adapters.DriverOrdersListAdapter;
import com.gmail.dleemcewen.tandemfieri.Entities.Order;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Repositories.Orders;
import com.gmail.dleemcewen.tandemfieri.Repositories.Restaurants;
import com.gmail.dleemcewen.tandemfieri.Tasks.TaskResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class DriverOrdersFragment extends DialogFragment{
    private Fragment self;
    private String driverId;
    private String restaurantId;
    private RelativeLayout myDeliveriesLayout;
    private ListView myDeliveriesList;
    private DriverOrdersListAdapter listAdapter;
    private Button closeMyOrders;
    private Button selectCurrentDelivery;
    private Orders<Order> ordersRepository;
    private Restaurants<Restaurant> restaurantsRepository;

    /**
     * Default constructor
     */
    public DriverOrdersFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param driverId driverId uniquely identifies the driver
     * @return A new instance of fragment DriverOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverOrdersFragment newInstance(String driverId) {
        DriverOrdersFragment fragment = new DriverOrdersFragment();
        Bundle args = new Bundle();
        args.putString("driverId", driverId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ordersRepository = new Orders<>(getActivity());
        restaurantsRepository = new Restaurants<>(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_orders, container, false);

        initialize(view);
        findControlReferences(view);
        bindEventHandlers();
        retrieveData();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * initialize all necessary variables
     */
    private void initialize(View view) {
        if (getArguments() != null) {
            driverId = getArguments().getString("driverId");
            restaurantId = getArguments().getString("restaurantId");
        }
    }

    /**
     * find all control references
     */
    private void findControlReferences(View view) {
        myDeliveriesLayout = (RelativeLayout)view.findViewById(R.id.myDeliveriesLayout);
        myDeliveriesList = (ListView)myDeliveriesLayout.findViewById(R.id.myDeliveriesList);
        closeMyOrders = (Button)myDeliveriesLayout.findViewById(R.id.closeMyOrders);
        selectCurrentDelivery = (Button)myDeliveriesLayout.findViewById(R.id.selectCurrentDelivery);
    }

    /**
     * bind required event handlers
     */
    private void bindEventHandlers() {
        closeMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverOrdersFragment.this.dismiss();
            }
        });

        selectCurrentDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverOrdersFragment.this.dismiss();
            }
        });
    }

    /**
     * retrieve data
     */
    private void retrieveData() {
        //First get the restaurant the driver is associated with
        restaurantsRepository
        .find("id = '" + restaurantId + "'")
        .addOnCompleteListener(getActivity(), new OnCompleteListener<TaskResult<Restaurant>>() {
            @Override
            public void onComplete(@NonNull Task<TaskResult<Restaurant>> task) {
                List<Restaurant> restaurants = task.getResult().getResults();
                String ownerId = "";

                if (!restaurants.isEmpty()) {
                    ownerId = restaurants.get(0).getOwnerId();
                }

                //Now with the ownerid we can get all the orders for the restaurant owner
                //additionally filter them by restaurantid because the order could be for a
                //restaurant owned by the owner that the driver isn't associated with
                ordersRepository
                    .atNode(ownerId)
                    .find("restaurantId = '" + restaurantId + "'")
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<TaskResult<Order>>() {
                        @Override
                        public void onComplete(@NonNull Task<TaskResult<Order>> task) {
                            List<Order> restaurantOrders = task.getResult().getResults();

                            if (!restaurantOrders.isEmpty()) {
                                List<Order> driverOrders = new ArrayList<>();

                                //TODO: need way to identify which of these orders should be associated with a driver
                                //for now, just assign all of them
                                driverOrders.addAll(restaurantOrders);

                                //bind the orders to the listview
                                listAdapter = new DriverOrdersListAdapter(getActivity(), driverOrders);
                                myDeliveriesList.setAdapter(listAdapter);
                            }
                        }
                    });
            }
        });
    }
}
