package com.gmail.dleemcewen.tandemfieri.Query;

/**
 * QueryBuilder
 */

public class QueryBuilder {
    public RepositoryQuery build(Class queryClass, String queryString) {
        //TODO: parse queryString
        return new RepositoryQuery("ownerId", queryString, queryClass);
    }
}
