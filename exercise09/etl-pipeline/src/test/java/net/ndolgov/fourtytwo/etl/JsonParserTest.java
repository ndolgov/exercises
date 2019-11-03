package net.ndolgov.fourtytwo.etl;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.spark.sql.types.DataTypes.StringType;
import static org.apache.spark.sql.functions.*;

public class JsonParserTest {
    @Test
    public void test() throws Exception {
        final SparkSession spark = SparkCtxCfg.createSparkSession();
        spark.sparkContext().setLogLevel("ERROR");

        final Dataset<Row> df = loadTestJsonEvents(spark);
        final Dataset<Row> parsedEvents = EtlPipelineBuilder.parseAndAugment(df);

        final Dataset<Row> aggregatedByExprEvents = parsedEvents
                .groupBy("device", "metric", "hour")
                .agg(
                    min("value").as("min"),
                    max("value").as("max"),
                    avg("value").as("avg"),
                    count(lit("1")).as("count"))
                .sort("hour", "device", "metric");

        logDatasetInfo(aggregatedByExprEvents);

        parsedEvents.createOrReplaceTempView("staged_iot_events");
        final String sql =
                " select hour, device, metric, min(value) as min, max(value) as max, avg(value) as avg, count(*) as count\n" +
                " from staged_iot_events\n" +
                " where month='2019-10' and day=28\n" +
                " group by device, metric, hour\n" +
                " order by hour, device, metric\n";
        final Dataset<Row> aggregatedBySqlEvents = spark.sql(sql);

        logDatasetInfo(aggregatedBySqlEvents);
    }

    private static void logDatasetInfo(Dataset<Row> aggregatedByExprEvents) {
        System.out.println(aggregatedByExprEvents.schema().treeString());
        System.out.println(aggregatedByExprEvents.count());
        aggregatedByExprEvents.show();
    }

    private Dataset<Row> loadTestJsonEvents(SparkSession spark) throws Exception {
        final Path path = Paths.get(getClass().getClassLoader().getResource("events.json").toURI());
        final Stream<String> lines = Files.lines(path);
        final List<Row> rows = lines.map(RowFactory::create).collect(Collectors.toList());

        return spark.createDataFrame(
            rows,
            new StructType(new StructField[]{new StructField("value", StringType, false, Metadata.empty())}));
    }
}
