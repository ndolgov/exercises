package net.ndolgov.contichallenge

import org.apache.calcite.adapter.jdbc.JdbcSchema
import org.apache.calcite.jdbc.CalciteConnection
import java.sql.DriverManager
import java.util.Properties


/** A sanity check with alternative API */
@Deprecated
object Main2 {

  def main(args: Array[String]): Unit = {
    Class.forName("org.apache.calcite.jdbc.Driver")
    val info = new Properties()
    info.setProperty("lex", "JAVA")
    val connection = DriverManager.getConnection("jdbc:calcite:", info)

    val calciteConnection = connection.unwrap(classOf[CalciteConnection])
    val rootSchema = calciteConnection.getRootSchema

    val schemaName = "public"
    val dataSource = JdbcSchema.dataSource("jdbc:postgresql://localhost/foodmart", "org.postgresql.Driver", "foodmart", "foodmart")
    val jdbcSchema = JdbcSchema.create(rootSchema, schemaName, dataSource, null, schemaName)
    rootSchema.add(schemaName, jdbcSchema)

    val statement = calciteConnection.createStatement
    val resultSet = statement.executeQuery(
      "select sum(sf.unit_sales) from public.sales_fact_1998 sf, public.customer c where sf.customer_id=c.customer_id and c.city='Albany'")

    print(ResultSetFormatter(resultSet, 5))
    resultSet.close()
    statement.close()
  }

}