package net.ndolgov.fourtytwo.queryserver.service;

import net.ndolgov.fourtytwo.queryserver.domain.ResultRow;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface QueryService {
    /**
     * Execute a query against the entire tenant dataset
     * @param sqlQuery Spark SQL query to execute
     * @return the query execution result
     */
    CompletableFuture<Collection<ResultRow>> executeQuery(String sqlQuery);
}
