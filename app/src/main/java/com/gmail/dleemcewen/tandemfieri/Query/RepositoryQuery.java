package com.gmail.dleemcewen.tandemfieri.Query;

/**
 * RepositoryQuery
 */

public class RepositoryQuery {
    private String field;
    private Object value;
    private Class queryClass;

    public RepositoryQuery(String field, Object value, Class queryClass) {
        this.field = field;
        this.value = value;
        this.queryClass = queryClass;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
       return value;
    }

    public Class getQueryClass() {
        return queryClass;
    }
}
