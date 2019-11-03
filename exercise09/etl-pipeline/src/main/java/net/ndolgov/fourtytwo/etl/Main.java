package net.ndolgov.fourtytwo.etl;

import org.apache.spark.sql.SparkSession;

public class Main {
    public static void main(String[] args) {
        final SparkSession spark = SparkCtxCfg.createSparkSession();
        spark.sparkContext().setLogLevel("ERROR");

        final String window = "5 minutes";
        final String stagingDirRoot = "/tmp/staging";
        final String checkpointDir = "/tmp/checkpoint";

        final EtlPipeline pipeline = new EtlPipeline(EtlPipelineBuilder.build(spark, window, stagingDirRoot, checkpointDir));
        System.out.println("Running ETL streaming pipeline to: " + checkpointDir);
        pipeline.run();
    }
}
