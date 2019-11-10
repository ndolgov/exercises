package net.ndolgov.contichallenge

import org.apache.calcite.tools.RelBuilder
import org.apache.calcite.plan.RelOptUtil
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.core.JoinRelType
import org.apache.calcite.sql.fun.SqlStdOperatorTable

/** Run test queries, print out query logical plans and execution results */
object Main {

  def main(args: Array[String]): Unit = {
    val dbSchema = DbSchema()

    val query1 = queryOne(dbSchema.builder)
    println(RelOptUtil.toString(query1))
    println(ResultSetFormatter.toString(RelNodeCompiler.run(query1, dbSchema.schemaName, dbSchema.schema)))

    val query2 = queryTwo(dbSchema.builder)
    println(RelOptUtil.toString(query2))
    println(ResultSetFormatter(RelNodeCompiler.run(query2, dbSchema.schemaName, dbSchema.schema).executeQuery(), 15))

    val query3 = queryThree(dbSchema.builder)
    println(RelOptUtil.toString(query3))
    println(ResultSetFormatter(RelNodeCompiler.run(query3, dbSchema.schemaName, dbSchema.schema).executeQuery(), 10))
  }

  /**
    * select sum(sf.unit_sales) from sales_fact_1998 sf, customer c where sf.customer_id=c.customer_id and c.city='Albany';
    */
  private[contichallenge] def queryOne(builder: RelBuilder): RelNode = {
    builder
      .scan("sales_fact_1998").as("sf")
      .project(
        builder.field("customer_id"),
        builder.field("unit_sales"))
      .scan("customer").as("c")
      .filter(builder.call(
        SqlStdOperatorTable.EQUALS,
        builder.field("city"),
        builder.literal("Albany")))
      .project(
        builder.field("customer_id"),
        builder.field("city"))
      .join(JoinRelType.INNER, "customer_id")
      .aggregate(
        builder.groupKey(),
        builder.sum(builder.field("unit_sales")).as("Albany sales"))
      .build
  }

  /**
    * Strictly speaking it should be:
    *
    * select the_year, month_of_year, sum(sf.unit_sales) from sales_fact_1998 sf, customer c, time_by_day t where sf.customer_id=c.customer_id and c.city='Albany' and t.time_id=sf.time_id group by t.the_year, t.month_of_year;
    *
    * .. but there's data for 1998 only so ignore the years column:
    *
    * select month_of_year, sum(sf.unit_sales) from sales_fact_1998 sf, customer c, time_by_day t where sf.customer_id=c.customer_id and c.city='Albany' and t.time_id=sf.time_id group by t.month_of_year
    */
  private[contichallenge] def queryTwo(builder: RelBuilder): RelNode = {
    builder
      .scan("sales_fact_1998").as("sf")
      .project(
        builder.field("customer_id"),
        builder.field("unit_sales"),
        builder.field("time_id"))
      .scan("customer").as("c")
      .filter(builder.call(
        SqlStdOperatorTable.EQUALS,
        builder.field("city"),
        builder.literal("Albany")))
      .project(builder.field("customer_id"), builder.field("city"))
      .join(JoinRelType.INNER, "customer_id")
      .scan("time_by_day").as("t")
      .project(
        builder.field("time_id")/*, builder.field("the_year")*/,
        builder.field("month_of_year"))
      .join(JoinRelType.INNER, "time_id")
      .aggregate(
        builder.groupKey(/*"the_year",*/ "month_of_year"),
        builder.sum(builder.field("unit_sales")).as("Monthly sales"))
      .build
  }

  /**
    * select c2.fname, c2.lname from customer c2 join (select c.customer_id from sales_fact_1998 sf, customer c where sf.customer_id=c.customer_id and c.city='Albany' group by c.customer_id order by sum(sf.unit_sales) DESC limit 5) as r on c2.customer_id=r.customer_id;
    *
    * .. or it could be
    * select c2.fname, c2.lname from customer c2 where c2.customer_id in (select c.customer_id from sales_fact_1998 sf, customer c where sf.customer_id=c.customer_id and c.city='Albany' group by c.customer_id order by sum(sf.unit_sales) DESC limit 5);
    */
  private[contichallenge] def queryThree(builder: RelBuilder): RelNode = builder
    .scan("sales_fact_1998").as("sf")
    .project(
      builder.field("customer_id"),
      builder.field("unit_sales"),
      builder.field("time_id"))
    .scan("customer").as("c")
    .filter(builder.call(
      SqlStdOperatorTable.EQUALS,
      builder.field("city"),
      builder.literal("Albany")))
    .project(
      builder.field("customer_id"),
      builder.field("city"))
    .join(JoinRelType.INNER, "customer_id")
    .aggregate(
      builder.groupKey("customer_id"),
      builder.sum(builder.field("unit_sales")).as("Monthly sales"))
    .sortLimit(0, 5, builder.desc(builder.field("Monthly sales")))
    .scan("customer").as("c2")
    .project(
      builder.field("customer_id"),
      builder.field("fname"),
      builder.field("lname"))
    .join(JoinRelType.INNER, "customer_id")
    .project(
      builder.field("fname"),
      builder.field("lname"))
    .build
}