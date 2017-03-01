package com.gmail.dleemcewen.tandemfieri.Tasks;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.gmail.dleemcewen.tandemfieri.Abstracts.Entity;
import com.gmail.dleemcewen.tandemfieri.Constants.NotificationConstants;
import com.gmail.dleemcewen.tandemfieri.Services.NotificationService;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseError;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * SendNotificationTask
 */

public class SendNotificationTask<T extends Entity> implements Continuation<Map.Entry<Boolean, DatabaseError>, Task<TaskResult<T>>> {
    private Context context;
    private NotificationConstants.Action action;
    private T entity;


    /**
     * Default constructor
     * @param context indicates the current application context
     * @param action indicates the previous task action
     * @param entity indicate the entity
     */
    public SendNotificationTask(Context context, NotificationConstants.Action action, T entity) {
        this.context = context;
        this.action = action;
        this.entity = entity;
    }

    @Override
    public Task<TaskResult<T>> then(@NonNull Task<Map.Entry<Boolean, DatabaseError>> task) throws Exception {
        final TaskCompletionSource<TaskResult<T>> taskCompletionSource = new TaskCompletionSource<>();

        //only check the database if the result from the previous task was successful
        if (task.getResult().getKey()) {
            Intent intent = new Intent(context, NotificationService.class);
            intent.setAction(action.toString());
            intent.putExtra("notificationClass", entity.getClass());
            intent.putExtra("entity", (Serializable) entity);
            intent.putExtra("key", entity.getKey());
            context.startService(intent);

            taskCompletionSource.setResult(new TaskResult<T>(action.toString(), Arrays.asList(entity), null));
        } else {
            taskCompletionSource.setResult(new TaskResult<T>(task.getResult().getValue()));
        }

        return taskCompletionSource.getTask();
    }
}

