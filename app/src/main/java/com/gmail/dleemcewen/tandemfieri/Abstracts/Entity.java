package com.gmail.dleemcewen.tandemfieri.Abstracts;

import com.google.firebase.database.Exclude;
import java.util.UUID;

/**
 * BaseEntity provides the application logic used by all entities for database access
 */
public abstract class Entity {
    private String key;

    /**
     * Default constructor
     */
    public Entity() {
        key = UUID.randomUUID().toString();
    }

    @Exclude
    public String getKey() {
        return key.toString();
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
