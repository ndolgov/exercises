package net.ndolgov.fourtytwo.etl;

import org.apache.spark.sql.SparkSession;

public final class SparkCtxCfg {
    public static SparkSession createSparkSession() {
        return SparkSession.
            builder().
            appName("ETL" + System.currentTimeMillis()).
            master("local[1]").
            config(SparkCtxCfg.SPARK_EXECUTOR_MEMORY, "1g").
            config(SparkCtxCfg.SPARK_SERIALIZER, SparkCtxCfg.KRYO).
            config(SparkCtxCfg.SPARK_SQL_SHUFFLE_PARTITIONS, "2").
            config(SparkCtxCfg.SPARK_WAREHOUSE_DIR, "target/spark-warehouse").
            //config(SparkCtxCfg.SPARK_JARS, SparkCtxCfg.toAbsolutePaths("", "")).
            config(SparkCtxCfg.SPARK_DRIVER_HOST, "localhost").
            config(SparkCtxCfg.SPARK_DRIVER_PORT, "31000").
            getOrCreate();
    }

    final static String SPARK_EXECUTOR_MEMORY = "spark.executor.memory";

    final static String SPARK_SERIALIZER = "spark.serializer";

    final static String ALLOW_MULTIPLE_CONTEXTS = "spark.driver.allowMultipleContexts";

    final static String SPARK_JARS = "spark.jars";

    final static String SPARK_WAREHOUSE_DIR = "spark.sql.warehouse.dir";

    final static String KRYO = "org.apache.spark.serializer.KryoSerializer";

    final static String SPARK_SQL_SHUFFLE_PARTITIONS = "spark.sql.shuffle.partitions";

    final static String DEFAULT_SPARK_MASTER_URL = "spark://127.0.0.1:7077";

    final static String SPARK_DRIVER_HOST = "spark.driver.host";

    final static String SPARK_DRIVER_PORT = "spark.driver.port";
}
