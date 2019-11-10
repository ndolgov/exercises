package net.ndolgov.contichallenge

import org.apache.calcite.adapter.jdbc.JdbcSchema
import org.apache.calcite.schema.Schema
import org.apache.calcite.tools.{Frameworks, RelBuilder}

/**
  * (A)
  * CREATE DATABASE foodmart;
  * CREATE USER foodmart WITH ENCRYPTED PASSWORD 'foodmart';
  * GRANT ALL PRIVILEGES ON DATABASE foodmart TO foodmart;
  *
  * (B)
  * Executing "sh FoodMartLoader.sh --db postgres" results in the following artifacts in postgres:
  *
  * foodmart=# select current_database(), current_schema();
  * current_database | current_schema
  * ------------------+----------------
  * foodmart         | public
  *
  * This object configures Calcite to access that database.
  */
object DbSchema {
  private val schemaName = "public"

  private val databaseName = "foodmart"

  def apply(): DbSchema = {
    apply(s"jdbc:postgresql://localhost/$databaseName", "foodmart", "foodmart", "org.postgresql.Driver", schemaName)
  }

  def apply(url: String, username: String, password: String, driver: String, schema: String): DbSchema = {
    val rootSchema = Frameworks.createRootSchema(true)

    val dataSource = JdbcSchema.dataSource(url, driver, username, password)
    val jdbcSchema = JdbcSchema.create(rootSchema, schema, dataSource, null, schema)
    val addedSchema = rootSchema.add(schema, jdbcSchema)

    val config = Frameworks.newConfigBuilder.defaultSchema(addedSchema).build
    DbSchema(RelBuilder.create(config), schemaName, jdbcSchema)
  }
}

case class DbSchema(builder: RelBuilder, schemaName: String, schema: Schema)