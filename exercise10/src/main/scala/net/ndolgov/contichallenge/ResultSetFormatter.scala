package net.ndolgov.contichallenge

import java.sql.{PreparedStatement, ResultSet, ResultSetMetaData, SQLException}

// borrowed from inaccessible org.apache.calcite.test.ResultSetFormatter
class ResultSetFormatter {
  private val buf = new StringBuilder

  @throws[SQLException]
  def resultSet(resultSet: ResultSet, maxRows: Int): ResultSetFormatter = {
    val metaData = resultSet.getMetaData
    var i = 0
    while ( resultSet.next && (i < maxRows)) {
      rowToString(resultSet, metaData)
      buf.append("\n")
      i = i + 1
    }
    this
  }

  /** Converts one row to a string. */
  @throws[SQLException]
  private def rowToString(resultSet: ResultSet, metaData: ResultSetMetaData) = {
    val n = metaData.getColumnCount
    if (n > 0) for (i <- 1 to n) {
      buf.append(" | ").append(metaData.getColumnLabel(i)).append("=").append(resultSet.getString(i))
    }
    buf.append(" |")
    this
  }

  /** Flushes the buffer and returns its previous contents. */
  def string: String = {
    val s = buf.toString
    buf.setLength(0)
    s
  }

  @throws[SQLException]
  def resultField(resultSet: ResultSet, fieldName: String): String = {
    if (!resultSet.next) {
      throw new IllegalArgumentException(s"No rows were returned")
    }

    val metaData = resultSet.getMetaData

    val n = metaData.getColumnCount
    if (n > 0) for (i <- 1 to n) {
      if (metaData.getColumnLabel(i) == fieldName) {
        return resultSet.getString(i)
      }
    }

    throw new IllegalArgumentException(s"Column not found: $fieldName")
  }
}

object ResultSetFormatter {
  @throws[SQLException]
  def apply(resultSet: ResultSet, maxRows: Int = Int.MaxValue): String = {
      new ResultSetFormatter().resultSet(resultSet, maxRows).string
  }

  @throws[SQLException]
  def apply(resultSet: ResultSet, fieldName: String): String = {
    new ResultSetFormatter().resultField(resultSet, fieldName)
  }

  def toString(statement: PreparedStatement): String = {
    try {
      ResultSetFormatter(statement.executeQuery, 5)
    } finally {
      try {
        statement.close()
      } catch {
        case e: Exception => println(e.getMessage)
      }
    }
  }
}
