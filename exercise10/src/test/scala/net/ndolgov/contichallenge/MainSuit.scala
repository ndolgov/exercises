package net.ndolgov.contichallenge

import org.scalatest.{Assertions, FlatSpec}

final class MainSuit extends FlatSpec with Assertions {
  private val dbSchema = DbSchema()

  "Q1 " should "sum up Albany sales" in {
    val query1 = Main.queryOne(dbSchema.builder)

    val salesSum = ResultSetFormatter(RelNodeCompiler.run(query1, dbSchema.schemaName, dbSchema.schema).executeQuery, "Albany sales")
    //println(s"Total Albany sales: $salesSum")
    assert(salesSum == "5544.0000")
  }

  "Q2 " should "sum up Albany sales by month" in {
    val query = Main.queryTwo(dbSchema.builder)

    val report = ResultSetFormatter(RelNodeCompiler.run(query, dbSchema.schemaName, dbSchema.schema).executeQuery, 15)
    //println(report)
    assert(report.contains("| month_of_year=1 | Monthly sales=537.0000 |"))
    assert(report.contains("| month_of_year=2 | Monthly sales=456.0000 |"))
    assert(report.contains("| month_of_year=3 | Monthly sales=400.0000 |"))
    assert(report.contains("| month_of_year=4 | Monthly sales=616.0000 |"))
    assert(report.contains("| month_of_year=5 | Monthly sales=446.0000 |"))
    assert(report.contains("| month_of_year=6 | Monthly sales=530.0000 |"))
    assert(report.contains("| month_of_year=7 | Monthly sales=477.0000 |"))
    assert(report.contains("| month_of_year=8 | Monthly sales=417.0000 |"))
    assert(report.contains("| month_of_year=9 | Monthly sales=576.0000 |"))
    assert(report.contains("| month_of_year=10 | Monthly sales=479.0000 |"))
    assert(report.contains("| month_of_year=11 | Monthly sales=610.0000 |"))
  }

  "Q3 " should "find top 5 customers from Albany" in {
    val query = Main.queryThree(dbSchema.builder)

    val topFiveNames = Seq(
        "| fname=Roslyn | lname=Jasper |",
        "| fname=Kathryn | lname=Chamberlin |",
        "| fname=Lawrence | lname=Roskey |",
        "| fname=Jack | lname=Tapin |",
        "| fname=Martha | lname=Parker |"
    )

    val report = ResultSetFormatter(RelNodeCompiler.run(query, dbSchema.schemaName, dbSchema.schema).executeQuery, 15)
    //println(report)

    var prev = 0
    for (name <- topFiveNames) {
      val next = report.indexOf(name)
      assert(next > prev)
      prev = next
    }
  }
}