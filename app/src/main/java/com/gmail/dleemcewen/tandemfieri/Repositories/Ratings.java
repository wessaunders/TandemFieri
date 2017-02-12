package com.gmail.dleemcewen.tandemfieri.Repositories;

import android.util.Log;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.Abstracts.Repository;
import com.gmail.dleemcewen.tandemfieri.Entities.Rating;
import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Ratings repository defines the database logic to use when adding, removing, or updating a Rating
 */

public class Ratings<T extends Entity> extends Repository<Rating> {
    private DatabaseReference dataContext;

    /**
     * find entities from the database
     *
     * @param childNodes      identifies the list of string arguments that indicates the
     *                        child node(s) that identify the location of the desired data
     * @param values          indicates the values to search for
     * @param onQueryComplete identifies the QueryCompleteListener to push results back to
     */
    @Override
    public void find(List<String> childNodes, List<String> values, QueryCompleteListener<Rating> onQueryComplete) {
        DatabaseReference dataContext = FirebaseDatabase
                .getInstance()
                .getReference(Rating.class.getSimpleName());

        String[] childNodesArray = new String[childNodes.size()];
        childNodesArray = childNodes.toArray(childNodesArray);

        Query query = buildRangeQuery(dataContext, values.get(0), values.get(1), childNodesArray);
        final QueryCompleteListener<Rating> finalQueryCompleteListener = onQueryComplete;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Rating> restaurants = new ArrayList<>();

                for (DataSnapshot record : dataSnapshot.getChildren()) {
                    Rating foundRating = record.getValue(Rating.class);
                    foundRating.setKey(record.getKey());

                    restaurants.add(foundRating);
                }

                finalQueryCompleteListener.onQueryComplete(restaurants);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Rating failed, log a message
                Log.w("Ratings", "Ratings.find:onCancelled", databaseError.toException());
            }
        });
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
    public Task<ArrayList<Rating>> find(List<String> childNodes, String value) {
        return null;
    }
}
