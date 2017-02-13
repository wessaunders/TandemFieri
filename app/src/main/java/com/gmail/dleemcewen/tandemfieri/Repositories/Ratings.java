package com.gmail.dleemcewen.tandemfieri.Repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.Abstracts.Repository;
import com.gmail.dleemcewen.tandemfieri.Entities.Rating;
import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
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
        search(childNodes, values, onQueryComplete);
    }

    /**
     * find entities from the database
     *
     * @param childNodes identifies the list of string arguments that indicates the
     *                   child node(s) that identify the location of the desired data
     * @param values      indicates the data values to search for
     * @return Task containing the results of the find that can be chained to other tasks
     */
    @Override
    public Task<ArrayList<Rating>> find(List<String> childNodes, List<String> values) {
        final List<String> childNodesReference = childNodes;
        final List<String> valuesReference = values;

        return Tasks.<Void>forResult(null)
                .continueWithTask(new Continuation<Void, Task<ArrayList<Rating>>>() {
                    @Override
                    public Task<ArrayList<Rating>> then(@NonNull Task<Void> task) throws Exception {
                        final TaskCompletionSource<ArrayList<Rating>> taskCompletionSource = new TaskCompletionSource<ArrayList<Rating>>();

                        search(childNodesReference, valuesReference, new QueryCompleteListener<Rating>() {
                            @Override
                            public void onQueryComplete(ArrayList<Rating> entities) {
                                taskCompletionSource.setResult(entities);
                            }
                        });

                        return taskCompletionSource.getTask();
                    }
                });
    }

    /**
     * execute a search from the database
     * @param childNodes identifies the list of string arguments that indicates the
     *                         child node(s) that identify the location of the desired data
     * @param values indicates the values to search for
     * @param onQueryComplete identifies the QueryCompleteListener to push results back to
     */
    private void search(List<String> childNodes, List<String> values, QueryCompleteListener<Rating> onQueryComplete) {
        DatabaseReference dataContext = FirebaseDatabase
                .getInstance()
                .getReference(Rating.class.getSimpleName());

        String[] childNodesArray = new String[childNodes.size()];
        childNodesArray = childNodes.toArray(childNodesArray);

        Query query;
        if (values.size() == 1) {
            query = buildEqualsQuery(dataContext, values.get(0), childNodesArray);
        } else {
            query = buildRangeQuery(dataContext, values.get(0), values.get(1), childNodesArray);
        }
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
}
