package com.gmail.dleemcewen.tandemfieri.Repositories;

import android.content.Context;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.Abstracts.Repository;
import com.gmail.dleemcewen.tandemfieri.Entities.Delivery;
import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Deliveries repository defines the database logic to use when adding, removing, or updating a Delivery
 */

public class Deliveries<T extends Entity> extends Repository<Delivery> {
    private Context context;

    /**
     * Default constructor
     * @param context indicates the current application context
     */
    public Deliveries(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * find entities from the database
     *
     * @param childNodes      identifies the list of string arguments that indicates the
     *                        child node(s) that identify the location of the desired data
     * @param value           indicates the data value to search for
     * @param onQueryComplete identifies the QueryCompleteListener to push results back to
     */
    @Override
    public void find(List<String> childNodes, String value, QueryCompleteListener<Delivery> onQueryComplete) {

    }

    /**
     * find entities from the database
     *
     * @param childNodes identifies the list of string arguments that indicates the
     *                   child node(s) that identify the location of the desired data
     * @param value      indicates the data value to search for
     * @return Task containing the results of the find that can be chained to other tasks
     */
    @Override
    public Task<ArrayList<Delivery>> find(List<String> childNodes, String value) {
        return null;
    }
}