package net.ndolgov.exercise

import java.io.File

import org.apache.spark.sql.SparkSession

/**
  * Auxiliary routines used in test fixture creation
  */
object SparkBootstrap {
  def sparkSession(name : String) : SparkSession = {
    val cpuCount = Runtime.getRuntime.availableProcessors() // try to use all cores

    SparkSession.builder().
      appName(name).
      master("local[" + cpuCount+ "]").
      config(SparkCtxCfg.SPARK_EXECUTOR_MEMORY, "4g").
      config(SparkCtxCfg.SPARK_SERIALIZER, SparkCtxCfg.KRYO).
      config(SparkCtxCfg.SPARK_SQL_SHUFFLE_PARTITIONS, cpuCount).
      config(SparkCtxCfg.SPARK_WAREHOUSE_DIR, "target/spark-warehouse").
      config(SparkCtxCfg.SPARK_JARS, SparkCtxCfg.toAbsolutePaths("", "")).
      getOrCreate()
  }
}

private object SparkCtxCfg {
  val SPARK_EXECUTOR_MEMORY = "spark.executor.memory"

  val SPARK_SERIALIZER = "spark.serializer"

  val ALLOW_MULTIPLE_CONTEXTS = "spark.driver.allowMultipleContexts"

  val SPARK_JARS = "spark.jars"

  val SPARK_WAREHOUSE_DIR = "spark.sql.warehouse.dir"

  val KRYO = "org.apache.spark.serializer.KryoSerializer"

  val SPARK_SQL_SHUFFLE_PARTITIONS = "spark.sql.shuffle.partitions"

  val DEFAULT_SPARK_MASTER_URL = "spark://127.0.0.1:7077"

  def envProperty(name : String, otherwise : String) : String = {
    val prop = System.getProperty(name)
    if (prop == null) otherwise else prop
  }

  def availableProcessors() : String = {
    Integer.toString(Runtime.getRuntime.availableProcessors())
  }

  def toAbsolutePaths(jarsString: String, baseDir: String): String = {
    if (jarsString == null || jarsString.length == 0) {
      return ""
    }
    val libDir: String = if (baseDir.endsWith(File.separator)) baseDir
    else baseDir + File.separator
    toAbsolutePaths(libDir, jarsString.split(",")).mkString(",")
  }

  private def toAbsolutePaths(libDir: String, jarFileNames: Array[String]): Array[String] = {
    jarFileNames.map(jar => libDir + jar)
  }
}
