package net.ndolgov.fourtytwo.etl;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.StreamingQuery;

public final class EtlPipeline {
    private final StreamingQuery pipelineQuery;

    public EtlPipeline(DataStreamWriter<Row> streamWriter) {
        this.pipelineQuery = streamWriter.start();
    }

    public void stop() {
        pipelineQuery.stop();
    }

    public void run() {
        try {
            pipelineQuery.awaitTermination();
        } catch (Exception e) {
            throw new RuntimeException("Streaming query stopped", e);
        }
    }
}
