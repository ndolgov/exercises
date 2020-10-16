package net.ndolgov.manalytics

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, from_json, udf, when}
import org.apache.spark.sql.types.{DataTypes, MapType, StructField, StructType}

/**
 * Load raw CSV files of known schemas.
 * When loading click stream data:
 * - apply heuristics to fix problems with JSON fields
 * - parse JSONfield into a Spark map column
 */
object CsvLoader {
  private val PurchasesSchema: StructType = StructType(Seq(
    StructField(PurchaseId, DataTypes.StringType, nullable = false),
    StructField(PurchaseTime, DataTypes.TimestampType, nullable = false),
    StructField(BillingCost, DataTypes.DoubleType, nullable = false),
    StructField(IsConfirmed, DataTypes.BooleanType, nullable = false)
  ))

  private val ClickstreamSchema: StructType = StructType(Seq(
    StructField(UserId, DataTypes.StringType, nullable = false),
    StructField("eventId", DataTypes.StringType, nullable = false),
    StructField(EventTime, DataTypes.TimestampType, nullable = false),
    StructField(EventType, DataTypes.StringType, nullable = false),
    StructField(Attributes, DataTypes.StringType, nullable = true)
  ))

  def loadPurchases(csvRoot: String, spark: SparkSession): DataFrame = {
    spark.read
      .option("header", "true")
      .option("delimiter", ",")
      .schema(PurchasesSchema)
      .csv(s"$csvRoot/purchases_sample.csv")
  }

  def loadClickStream(csvRoot: String, spark: SparkSession): DataFrame = {
    val toValidJson = udf[String, String](json => json
      .replace("{{", "{")
      .replace("}}", "}")
      .replace("“", "\"")
    )

    spark.read
      .option("header", "true")
      .option("delimiter", ",")
      .option("escape", "\"")
      .schema(ClickstreamSchema)
      .csv(s"$csvRoot/mobile-app-clickstream_sample.csv")
      .withColumn(
        Attributes
          ,when(col(Attributes).isNotNull,
            from_json(
              toValidJson(col(Attributes)),
              MapType(DataTypes.StringType, DataTypes.StringType)))
          .otherwise(null)
      )
  }
}
