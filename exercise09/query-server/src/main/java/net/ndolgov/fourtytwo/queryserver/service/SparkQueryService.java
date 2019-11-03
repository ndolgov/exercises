package net.ndolgov.fourtytwo.queryserver.service;

import net.ndolgov.fourtytwo.queryserver.domain.ResultRow;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class SparkQueryService implements QueryService {
    private final Logger log = LoggerFactory.getLogger(QueryService.class);

    private final SparkSession spark;

    private final String stagingDirRoot;

    public SparkQueryService(SparkSession spark, String stagingDirRoot) {
        this.spark = spark;
        this.stagingDirRoot = stagingDirRoot;
    }

    public CompletableFuture<Collection<ResultRow>> executeQuery(String sqlQuery) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Running SQL query: " + sqlQuery);

            final Dataset<Row> entireDataset = spark.read().parquet(stagingDirRoot);
            entireDataset.createOrReplaceTempView("staged_iot_events");

            final Dataset<Row> queryResults = spark.sql(sqlQuery);
            return queryResults.collectAsList().stream().map(this::toResultRow).collect(Collectors.toList());
        });
    }

    /**
     * Assume a static schema returned by a parameterized query
     * |-- hour: integer
     * |-- device: integer
     * |-- metric: integer
     * |-- min: double
     * |-- max: double
     * |-- avg: double
     * |-- count: long
     * @return row domain object that can be serialized to JSON
     */
    private ResultRow toResultRow(Row row) {
        final ResultRow resultRow = new ResultRow(
                row.getInt(0), row.getInt(1), row.getInt(2),
                row.getDouble(3), row.getDouble(4), row.getDouble(5),
                row.getLong(6));
        //log.info("Retrieved: " + resultRow);
        return resultRow;
    }
}
