package net.ndolgov.fourtytwo.etl;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import static org.apache.spark.sql.functions.from_json;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.Trigger;

import static org.apache.spark.sql.types.DataTypes.StringType;
import static org.apache.spark.sql.types.DataTypes.TimestampType;

/**
 * Collect Kafka messages to windows of configured duration.
 * Write the collected event windows partitioned on month, day, and hour to Parquet files.
 */
public final class EtlPipelineBuilder {
    private static final String COL_MONTH = "month"; // the columns to partition on
    private static final String COL_DAY = "day";
    private static final String COL_HOUR = "hour";

    private static final String COL_TIME = "time"; // the Timestamp representation of a long timestamp

    private static final String COL_VALUE = "value"; // the Kafka message value column

    private static final String COL_JSON = "json"; // a single IoT message as a JSON string

    private static final String COL_MESSAGE = "message"; // a struct with parsed InputReading fields inside

    public static DataStreamWriter<Row> build(SparkSession spark, String window, String stagingDirRoot, String checkpointDir) {
        final Dataset<Row> json = spark
            .readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("subscribe", "iot_device_events")
            .option("startingOffsets", "latest")
            .load();

        return parseAndAugment(json)
            .writeStream()
            .trigger(Trigger.ProcessingTime(window))
            .outputMode("append")
            .format("parquet")
            .option("path", stagingDirRoot)
            .option("checkpointLocation", checkpointDir)
            .partitionBy(COL_MONTH, COL_DAY, COL_HOUR);
    }

    public static Dataset<Row> parseAndAugment(Dataset<Row> json) {
        final Encoder<IoTEvent> encoder = Encoders.bean(IoTEvent.class);

        final Dataset<Row> jsonDF = json.select(json.col(COL_VALUE).cast(StringType).as(COL_JSON));

        final Dataset<Row> nestedDF = jsonDF.select(from_json(jsonDF.col(COL_JSON), encoder.schema()).as(COL_MESSAGE));

        final Dataset<Row> topLevelDF = nestedDF.select(COL_MESSAGE + ".*");

        final Dataset<Row> augmentedDF = topLevelDF.withColumn(COL_TIME, topLevelDF.col("timestamp").divide(1_000).cast(TimestampType));

        return augmentedDF
            .withColumn(COL_MONTH, functions.date_format(augmentedDF.col(COL_TIME), "YYYY-MM"))
            .withColumn(COL_DAY, functions.date_format(augmentedDF.col(COL_TIME), "dd"))
            .withColumn(COL_HOUR, functions.hour(augmentedDF.col(COL_TIME)))
            .coalesce(1);
    }
}
