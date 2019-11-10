package net.ndolgov.contichallenge

import java.sql.{DriverManager, PreparedStatement, SQLException}

import org.apache.calcite.jdbc.CalciteConnection
import org.apache.calcite.rel.RelNode
import org.apache.calcite.schema.Schema
import org.apache.calcite.tools.RelRunner

/**
  * A "org.apache.calcite.tools.RelRunners" that knows about our DB schema.
  *
  * Without adding our schema to the root schema you'll enjoy hours of seeing the following:
  *
  * Caused by: java.lang.NullPointerException
  * at Baz.bind(Unknown Source)
  * at org.apache.calcite.jdbc.CalcitePrepare$CalciteSignature.enumerable(CalcitePrepare.java:355)
  */
object RelNodeCompiler {
  def run(rel: RelNode, schemaName: String, schema: Schema): PreparedStatement = {
    val connection = DriverManager.getConnection("jdbc:calcite:")

    try {
      val calciteConnection = connection.unwrap(classOf[CalciteConnection])
      calciteConnection.getRootSchema.add(schemaName, schema)

      val runner = connection.unwrap(classOf[RelRunner])
      runner.prepare(rel)
    } catch {
      case e: SQLException =>
        throw new RuntimeException(e)
    } finally {
      if (connection != null) connection.close()
    }
  }
}
