package com.gmail.dleemcewen.tandemfieri.Abstracts;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gmail.dleemcewen.tandemfieri.EventListeners.QueryCompleteListener;
import com.gmail.dleemcewen.tandemfieri.Logging.LogWriter;
import com.gmail.dleemcewen.tandemfieri.Query.QueryBuilder;
import com.gmail.dleemcewen.tandemfieri.Query.RepositoryQuery;
import com.gmail.dleemcewen.tandemfieri.Tasks.AddEntitiesTask;
import com.gmail.dleemcewen.tandemfieri.Tasks.AddEntityTask;
import com.gmail.dleemcewen.tandemfieri.Tasks.GetEntitiesTask;
import com.gmail.dleemcewen.tandemfieri.Tasks.NetworkConnectivityCheckTask;
import com.gmail.dleemcewen.tandemfieri.Tasks.RemoveEntitiesTask;
import com.gmail.dleemcewen.tandemfieri.Tasks.RemoveEntityTask;
import com.gmail.dleemcewen.tandemfieri.Tasks.UpdateEntityTask;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Repository defines the abstract class that all repositories extend
 */
public abstract class Repository<T extends Entity> {
    private Context context;
    private List<T> entities;
    private DatabaseReference dataContext;
    private final Class<T> childClass;
    private List<String> searchNodes;

    /**
     * Default constructor
     * @param context identifies the current application context
     */
    public Repository(final Context context) {
        this.context = context;
        this.childClass = getChildClassType();
        entities = new ArrayList<>();
        searchNodes = new ArrayList<>();

        dataContext = getDataContext(childClass.getSimpleName());
        dataContext.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Entity childEntityRecord = dataSnapshot.getValue(childClass);
                childEntityRecord.setKey(dataSnapshot.getKey());

                entities.add((T)childEntityRecord);

                LogWriter.log(context, Level.FINEST, "added new child entity. " + entities.size() + " total entities in repository");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int matchingEntityIndex = -1;

                for (int index = 0; index < entities.size() && matchingEntityIndex < 0; index++) {
                    if (entities.get(index).getKey().equals(dataSnapshot.getKey()) ) {
                        matchingEntityIndex = index;
                    }
                }

                if (matchingEntityIndex > -1) {
                    //entities.set(matchingEntityIndex, convertInstanceOfObject(dataSnapshot.getValue(childClass), childClass));
                    entities.set(matchingEntityIndex, dataSnapshot.getValue(childClass));

                    LogWriter.log(context, Level.FINE, "updated existing child entity at index " + matchingEntityIndex + ". " + entities.size() + " total entities in repository");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int matchingEntityIndex = -1;

                for (int index = 0; index < entities.size() && matchingEntityIndex < 0; index++) {
                    if (entities.get(index).getKey().equals(dataSnapshot.getKey()) ) {
                        matchingEntityIndex = index;
                    }
                }

                if (matchingEntityIndex > -1) {
                    entities.remove(matchingEntityIndex);

                    LogWriter.log(context, Level.FINE, "removed child entity at index " + matchingEntityIndex + ". " + entities.size() + " total entities in repository");
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //not implemented
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });
    }

    /**
     * add a single entity to the database
     * @param entity indicates the entity to add
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     */
    public Task<Map.Entry<Boolean, DatabaseError>> add(T entity, String... childNodes) {
        dataContext = getDataContext(entity.getClass().getSimpleName(), childNodes);

        return Tasks.<Void>forResult(null)
            .continueWithTask(new NetworkConnectivityCheckTask(context))
            .continueWithTask(new AddEntityTask<T>(dataContext, entity, childNodes));
    }

    /**
     * add multiple entities to the database
     * @param entities indicates a list of entities to add
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     */
    public Task<Map.Entry<Boolean, DatabaseError>> add(List<T> entities, String... childNodes) {
        dataContext = getDataContext(entities.get(0).getClass().getSimpleName(), childNodes);

        return Tasks.<Void>forResult(null)
            .continueWithTask(new NetworkConnectivityCheckTask(context))
            .continueWithTask(new AddEntitiesTask<T>(dataContext, entities, childNodes));    }

    /**
     * updates the specified entity
     * @param entity indicates the entity to update with the new values
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     */
    public Task<Map.Entry<Boolean, DatabaseError>> update(T entity, String... childNodes) {
        dataContext = getDataContext(entity.getClass().getSimpleName(), childNodes);

        return Tasks.<Void>forResult(null)
            .continueWithTask(new NetworkConnectivityCheckTask(context))
            .continueWithTask(new UpdateEntityTask<T>(dataContext, entity));
    }

    /**
     * remove a single entity from the database
     * @param entity indicates the entity to remove
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     */
    public Task<Map.Entry<Boolean, DatabaseError>> remove(T entity, String... childNodes) {
        dataContext = getDataContext(entity.getClass().getSimpleName(), childNodes);

        return Tasks.<Void>forResult(null)
                .continueWithTask(new NetworkConnectivityCheckTask(context))
                .continueWithTask(new RemoveEntityTask<T>(dataContext, entity));
    }

    /**
     * remove multiple entities from the database
     * @param entities indicates a list of entities to remove
     * @param childNodes indicates the variable number of string arguments that identify the
     *                         child nodes that identify the location of the desired data
     */
    public Task<Map.Entry<Boolean, DatabaseError>> remove(List<T> entities, String... childNodes) {
        dataContext = getDataContext(entities.get(0).getClass().getSimpleName(), childNodes);

        return Tasks.<Void>forResult(null)
                .continueWithTask(new NetworkConnectivityCheckTask(context))
                .continueWithTask(new RemoveEntitiesTask<T>(dataContext, entities));
    }

    /**
     * searchAtNode describes the node a find should begin at
     * @param node indicates the node where a find should begin
     * @return
     */
    public Repository<T> searchAtNode(String node) {
        searchNodes.add(node);
        return this;
    }

    /**
     * find entities from the database
     * @param queryString identifies the query string to send to the database
     * @return all entities that match the provided query string
     */
    public List<T> find(final String queryString) {
        String[] childNodes = new String[searchNodes.size()];
        childNodes = searchNodes.toArray(childNodes);
        final List<T> matchedEntities = new ArrayList<>();

        dataContext = getDataContext(childClass.getSimpleName(), childNodes);

        QueryBuilder queryBuilder = new QueryBuilder();
        RepositoryQuery repositoryQuery = queryBuilder.build(childClass, queryString);

        if (queryString != null && !queryString.trim().equals("")) {
            for (T entity : entities) {

            }
        }

        if (matchedEntities.isEmpty()) {
            for (T entity : entities) {
                matchedEntities.add(entity);
            }
        }

        //Clear after find complete
        searchNodes.clear();

        return matchedEntities;
    }

    /**
     * find entities from the database
     * @param childNodes identifies the list of string arguments that indicates the
     *                         child node(s) that identify the location of the desired data
     * @param value indicates the data value to search for
     * @param onQueryComplete identifies the QueryCompleteListener to push results back to
     */
    public abstract void find(List<String> childNodes, String value, QueryCompleteListener<T> onQueryComplete);

    /**
     * find entities from the database
     * @param childNodes identifies the list of string arguments that indicates the
     *                         child node(s) that identify the location of the desired data
     * @param value indicates the data value to search for
     * @return Task containing the results of the find that can be chained to other tasks
     */
    public abstract Task<ArrayList<T>> find(List<String> childNodes, String value);

    /**
     * getDataContext gets a new data context pointing to the appropriate entity
     * at the appropriate nested child node
     * @param entityName indicates the name of the entity
     * @param childNodes indicates the list of child nodes that indicate the location
     *                         of the desired data (optional)
     * @return new Firebase database reference
     */
    protected DatabaseReference getDataContext(String entityName, String... childNodes) {
        DatabaseReference dataContext = FirebaseDatabase.getInstance().getReference(entityName);
        for (String childNode : childNodes) {
            dataContext = dataContext.child(childNode);
        }

        return dataContext;
    }

    /**
     * buildEqualsQuery builds the appropriate query that defines that the data at the provided childnodes
     * should be equal to the provided equalsValue
     * @param dataContext indicates the data context
     * @param equalsValue indicates the value to equal (optional)
     * @param childNodes indicates the array of child nodes that indicate the location
     *                         of the desired data (optional)
     * @return query that can be executed by firebase to find desired records
     */
    protected Query buildEqualsQuery(DatabaseReference dataContext, String equalsValue, String... childNodes) {
       Query query = buildQuery(dataContext, Arrays.asList(childNodes));

        if (equalsValue != null && !equalsValue.equals("")) {
            query = query.equalTo(equalsValue);
        }

        return query;
    }

    /**
     * buildRangeQuery builds the appropriate query that defines that the data at the provided childnodes
     * should be bounded by the provided startingRangeValue and the provided endingRangeValue
     * @param dataContext indicates the data context
     * @param startingRangeValue indicates the starting range value (optional)
     * @param endingRangeValue indicates the starting range value (optional)
     * @param childNodes indicates the array of child nodes that indicate the location
     *                         of the desired data (optional)
     * @return query that can be executed by firebase to find desired records
     */
    protected Query buildRangeQuery(DatabaseReference dataContext, String startingRangeValue, String endingRangeValue, String... childNodes) {
        Query query = buildQuery(dataContext, Arrays.asList(childNodes));

        if (!startingRangeValue.equals("")) {
            query = query.startAt(startingRangeValue);
        }

        if (!endingRangeValue.equals("")) {
            query = query.endAt(endingRangeValue);
        }

        return query;
    }

    /**
     * buildQuery builds the appropriate query based on the provided childNodes and value
     * @param dataContext indicates the data context
     * @param childNodes indicates the list of child nodes to query (optional)
     * @return query that can be executed by firebase to find desired records
     */
    private Query buildQuery(DatabaseReference dataContext, List<String> childNodes) {
        Query query;

        String childNodesPath = "";
        if (!childNodes.isEmpty()) {
            //Get the last values in childNodes and assign that to childnodespath
            childNodesPath = childNodes.get(childNodes.size() - 1);
        }

        //If there are any other child nodes, assign those as children on the datacontext
        //Otherwise queries on multi-node entities will not work
        if (!childNodes.isEmpty()) {
            for (int index = 0; index < childNodes.size() - 1; index++) {
                dataContext = dataContext.child(childNodes.get(index));
            }
        }

        if (!childNodesPath.equals("")) {
            query = dataContext.orderByChild(childNodesPath);
        } else {
            query = dataContext.orderByKey();
        }

        return query;
    }

    /**
     * getChildClassType gets the child class type from the generic type parameter
     * @return child class type
     */
    @SuppressWarnings("unchecked")
    private Class<T> getChildClassType() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * convertInstanceOfObject converts an object to the provided destination class
     * @param objectInstance indicates the instance of the object to convert
     * @param destinationClass identifies the destination class
     * @return object converted to destination class
     */
    private T convertInstanceOfObject(Object objectInstance, Class<T> destinationClass) {
        try {
            return destinationClass.cast(objectInstance);
        } catch(ClassCastException e) {
            return null;
        }
    }
}

