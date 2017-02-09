package com.gmail.dleemcewen.tandemfieri.Repositories;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.Abstracts.Repository;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.Entities.User;
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
import java.util.List;

/**
 * Restaurants repository defines the database logic to use when adding, removing, or updating a Restaurant
 */

public class Restaurants<T extends Entity> extends Repository<Restaurant> {
    private DatabaseReference dataContext;

    /**
     * add a single restaurant to the database
     * @param entity indicates the restaurant to add
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     * ex: restaurants.add(newRestaurant) or restaurants.add(newRestaurant, new String[] {"Downtown"}
     */
    @Override
    public void add(Restaurant entity, String... childNodes) {
        dataContext = getDataContext(entity.getClass().getSimpleName(), childNodes);
        dataContext.child(entity.getKey().toString()).setValue(entity);
    }

    /**
     * add multiple restaurants to the database
     * @param entities indicates a list of restaurants to add
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     */
    @Override
    public void add(ArrayList<Restaurant> entities, String... childNodes) {
        dataContext = getDataContext(entities.get(0).getClass().getSimpleName(), childNodes);
        for (Restaurant entity : entities) {
            dataContext.child(entity.getKey().toString()).setValue(entity);
        }
    }

    /**
     * updates the specified restaurant
     * @param entity indicates the restaurant to update with the new values
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     * ex: restaurants.update(existingRestaurant) or restaurants.update(existingRestaurant, new String[] {"Fastfood"}
     */
    @Override
    public void update(Restaurant entity, String... childNodes) {
        dataContext = getDataContext(entity.getClass().getSimpleName(), childNodes);
        dataContext.child(entity.getKey().toString()).setValue(entity);
    }

    /**
     * remove a single restaurant from the database
     * @param entity indicates the restaurant to remove
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     * ex: restaurants.remove(existingRestaurant) or restaurants.remove(existingRestaurant, new String[] {"Buffet"}
     */
    @Override
    public void remove(Restaurant entity, String... childNodes) {
        dataContext = getDataContext(entity.getClass().getSimpleName(), childNodes);
        dataContext.child(entity.getKey().toString()).removeValue();
    }

    /**
     * remove multiple restaurants from the database
     * @param entities indicates a list of restaurants to remove
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     */
    @Override
    public void remove(ArrayList<Restaurant> entities, String... childNodes) {
        dataContext = getDataContext(entities.get(0).getClass().getSimpleName(), childNodes);
        for (Restaurant entity : entities) {
            dataContext.child(entity.getKey().toString()).removeValue();
        }
    }

    /**
     * find restaurants from the database
     * @param childNodes identifies the list of string arguments that indicates the
     *                         child node(s) that identify the location of the desired data
     * @param value indicates the data value to search for
     * @param onQueryComplete identifies the QueryCompleteListener to push results back to
     * ex: to find a restaurant downtown on Broadway:
    users.find(Arrays.asList("Downtown", "Street"), "Broadway", new QueryCompleteListener<User>() {
    @Override
    public void onQueryComplete(ArrayList<User> entities) {
    // ...
    }
    });
     */
    @Override
    public void find(List<String> childNodes, String value, QueryCompleteListener<Restaurant> onQueryComplete) {
        search(childNodes, value, onQueryComplete);
    }

    /**
     * find restaurants from the database
     * @param childNodes identifies the list of string arguments that indicates the
     *                         child node(s) that identify the location of the desired data
     * @param value indicates the data value to search for
     * @return Task containing the results of the find that can be chained to other tasks
     */
    @Override
    public Task<ArrayList<Restaurant>> find(List<String> childNodes, String value) {
        final List<String> childNodesReference = childNodes;
        final String valueReference = value;

        return Tasks.<Void>forResult(null)
                .continueWithTask(new Continuation<Void, Task<ArrayList<Restaurant>>>() {
                    @Override
                    public Task<ArrayList<Restaurant>> then(@NonNull Task<Void> task) throws Exception {
                        final TaskCompletionSource<ArrayList<Restaurant>> taskCompletionSource = new TaskCompletionSource<ArrayList<Restaurant>>();

                        search(childNodesReference, valueReference, new QueryCompleteListener<Restaurant>() {
                            @Override
                            public void onQueryComplete(ArrayList<Restaurant> entities) {
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
     * @param value indicates the value to search for
     * @param onQueryComplete identifies the QueryCompleteListener to push results back to
     */
    private void search(List<String> childNodes, String value, QueryCompleteListener<Restaurant> onQueryComplete) {
        DatabaseReference dataContext = FirebaseDatabase
                .getInstance()
                .getReference(User.class.getSimpleName());

        Query query = buildQuery(dataContext, childNodes, value);
        final QueryCompleteListener<Restaurant> finalQueryCompleteListener = onQueryComplete;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

                for (DataSnapshot record : dataSnapshot.getChildren()) {
                    Restaurant foundRestaurant = record.getValue(Restaurant.class);
                    foundRestaurant.setKey(record.getKey());

                    restaurants.add(foundRestaurant);
                }

                finalQueryCompleteListener.onQueryComplete(restaurants);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Restaurant failed, log a message
                Log.w("Restaurants", "Restaurants.find:onCancelled", databaseError.toException());
            }
        });
    }

}
