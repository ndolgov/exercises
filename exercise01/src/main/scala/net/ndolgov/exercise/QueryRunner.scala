package net.ndolgov.exercise

import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.functions.countDistinct
import org.apache.spark.sql.functions.max
import org.apache.spark.sql.functions.min
import org.apache.spark.sql.functions.sum
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.{Dataset, Row, SparkSession}

/**
  * Execute exercise queries using a given data set represented by parquet files
  */
object QueryRunner {
  private val PUBLISHER = "publisher"
  val PHRASE = "phrase"
  val COUNT = "count"
  val TOTAL = "total"

  private val CLICK_TIME = "clickTime"
  private val VIEW_TIME = "viewTime"
  private val IMPRESSION_ID = "impressionId"
  private val DESTINATION = "destination"
  private val REFERRER = "referrer"
  private val VISITOR_ID = "visitorId"
  private val MATCHES = "matches"
  private val ID = "id"
  private val URL = "url"
  private val DELAY = "delay"
  val DOMAIN = "domain"

  private val INSERTS = "INSERTS"

  private val INVALID_URL_DOMAIN = "INVALID" // to return when publisher domain cannot be parsed
  private val INVALID_TIME_DIFFERENCE = Int.MaxValue // to return when clickTime is ahead of viewTime (presumably because clocks are out of sync)

  /**
    * Find the phrase (or phrases) that was inserted most frequently for each publisher. Phrases should be compared in a case insensitive manner
    *
    * @param insertsDir "Inserts" Parquet files location
    * @return dataset conforming to (PUBLISHER,PHRASE,TOTAL) schema
    */
  def frequentPhrases(session: SparkSession, insertsDir : String): Dataset[Row] = {
    import session.implicits._

    session.
      read.parquet(insertsDir).
      flatMap((row: Row) =>
        row.getSeq[GenericRowWithSchema](2).map(phraseToUrl => (row.getString(3), phraseToUrl.getString(0).toLowerCase, 1))).
      toDF(PUBLISHER, PHRASE, COUNT).
      groupBy(PUBLISHER, PHRASE).agg(sum(COUNT).alias(TOTAL)).
      createOrReplaceTempView(INSERTS)

    val sql = "SELECT %s, %s, %s FROM %s T1 WHERE %s = (SELECT max(%s) FROM %s T2 where T2.%s=T1.%s)".
      format(PUBLISHER, PHRASE, TOTAL, INSERTS, TOTAL, TOTAL, INSERTS, PUBLISHER, PUBLISHER)

    session.sql(sql)
  }

  /**
    * What are the most visited publisher domains by unique visitor counts
    *
    * @param impressionsDir "Impressions" Parquet files location
    * @return dataset conforming to (DOMAIN,COUNT) schema
    */
  def topPublisherDomains(session: SparkSession, impressionsDir : String): Dataset[Row] = {
    val extractDomain: (String) => String = url => {
      try {
        java.net.URI.create(url).getHost
      } catch {
        case e : Exception => INVALID_URL_DOMAIN // System.out.println("Invalid URL: " + url);
      }
    }

    val impressions = session.
      read.parquet(impressionsDir).select(VISITOR_ID, REFERRER).
      withColumn(DOMAIN, udf(extractDomain).apply(col(REFERRER))).
      groupBy(DOMAIN).agg(countDistinct(VISITOR_ID).alias(COUNT))

    impressions.sort(impressions(COUNT).desc)
  }

  /**
    * Find the fastest and the slowest impressions. TopN can be produced by calling "take(N)" on the results.
    *
    * @param clicksDir "Clicks" Parquet files location
    * @param impressionsDir "Impressions" Parquet files location
    * @return a pair of datasets that represent the slowest and the fastest impressions; DO NOT FORGET to take(N)
    */
  def clickDelays(session: SparkSession, clicksDir : String, impressionsDir : String): Tuple2[Dataset[Row], Dataset[Row]] = {
    val clicks = session.read.parquet(clicksDir).
      select(VISITOR_ID, DESTINATION, CLICK_TIME).
      withColumnRenamed(DESTINATION, URL)

    val impressions = session.read.parquet(impressionsDir).
      select(ID, VISITOR_ID, REFERRER, VIEW_TIME).
      withColumnRenamed(REFERRER, URL)

    val joined = clicks.join(impressions, Seq(URL, VISITOR_ID))

    val timeDifference: (Long, Long) => Long = (clickedAt: Long, viewedAt: Long) => {
      val delta = clickedAt - viewedAt
      Math.abs(delta)//if (delta > 0) delta else INVALID_TIME_DIFFERENCE
    }

    val withDelay = joined.
      withColumn(DELAY, udf(timeDifference).apply(joined(CLICK_TIME), joined(VIEW_TIME)))

    val validOnly = withDelay.where(withDelay(DELAY).notEqual(INVALID_TIME_DIFFERENCE))

    val minByImpression = validOnly.groupBy(ID).agg(min(DELAY).alias(DELAY))
    val topTen = minByImpression.sort(minByImpression(DELAY).asc)

    val maxByImpression = validOnly.groupBy(ID).agg(max(DELAY).alias(DELAY))
    val bottomTen = maxByImpression.sort(maxByImpression(DELAY).desc)

    (bottomTen, topTen)
  }

  /**
    * Compute the click-through-rate for each publisher that has at least a single impression event.
    *
    * @param clicksDir "Clicks" Parquet files location
    * @param impressionsDir "Impressions" Parquet files location
    * @return a pair of datasets that represent the slowest and the fastest impressions; DO NOT FORGET to take(N)
    */
  def computeCTRs(session: SparkSession, clicksDir : String, impressionsDir : String): Dataset[Row] = {
    val CLICK_COUNT = "clickCount"
    val VIEW_COUNT = "viewCount"
    val CTR = "CTR"

    val clicks = session.read.parquet(clicksDir).select(PUBLISHER).
    groupBy(PUBLISHER).agg(count("*").alias(CLICK_COUNT))

    val impressions = session.read.parquet(impressionsDir).select(PUBLISHER).
      groupBy(PUBLISHER).agg(count("*").alias(VIEW_COUNT))

    val ctr: (Long, Long) => Double = (clicks: Long, impressions: Long) => {
      "%.3f".format(clicks.toDouble / impressions.toDouble).toDouble
    }
    val joined = clicks.join(impressions, PUBLISHER).
      withColumn(CTR, udf(ctr).apply(clicks(CLICK_COUNT), impressions(VIEW_COUNT)))

    joined.sort(joined(VIEW_COUNT).desc, joined(CTR).desc)
  }

  private def log(msg : String, df : Dataset[Row]): Unit = {
    df.printSchema()
    val count = df.count()
    System.out.println(msg + count)
    df.show()
  }
}
