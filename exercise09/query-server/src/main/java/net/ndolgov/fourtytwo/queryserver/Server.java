package net.ndolgov.fourtytwo.queryserver;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import net.ndolgov.fourtytwo.queryserver.service.SparkQueryService;
import net.ndolgov.fourtytwo.queryserver.web.HttpEndpoint;
import org.apache.spark.sql.SparkSession;

/**
 * Query Server entry point
 */
public final class Server extends AllDirectives {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("QueryServer");

        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final String stagingDirRoot = "/tmp/staging"; // TODO configure

        Http.get(system)
            .bindAndHandle(
                new HttpEndpoint(
                    new SparkQueryService(SparkCtxCfg.createSparkSession(), stagingDirRoot),
                    EntityStreamingSupport.json(),
                    Jackson.marshaller())
                .routes().flow(system, materializer),
                ConnectHttp.toHost("localhost", 8080),
                materializer)
            .whenComplete((binding, e) -> {
                if (e == null) {
                    System.out.println("Server online at http://localhost:8080/");
                } else {
                    System.err.println("Server failed to start");
                    e.printStackTrace(System.err);
                    system.terminate();
                    System.exit(-1);
                }
            });
    }

    // TODO reuse?
    private static final class SparkCtxCfg {
        static SparkSession createSparkSession() {
            final SparkSession sparkSession = SparkSession.
                    builder().
                    appName("QueryServer" + System.currentTimeMillis()).
                    master("local[1]").
                    config(SparkCtxCfg.SPARK_EXECUTOR_MEMORY, "1g").
                    config(SparkCtxCfg.SPARK_SERIALIZER, SparkCtxCfg.KRYO).
                    config(SparkCtxCfg.SPARK_SQL_SHUFFLE_PARTITIONS, "2").
                    config(SparkCtxCfg.SPARK_WAREHOUSE_DIR, "target/spark-warehouse").
                    //config(SparkCtxCfg.SPARK_JARS, SparkCtxCfg.toAbsolutePaths("", "")).
                    config(SparkCtxCfg.SPARK_DRIVER_HOST, "localhost").
                    config(SparkCtxCfg.SPARK_DRIVER_PORT, "31010").
                    getOrCreate();

            //sparkSession.sparkContext().setLogLevel("ERROR");
            return sparkSession;
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
}


