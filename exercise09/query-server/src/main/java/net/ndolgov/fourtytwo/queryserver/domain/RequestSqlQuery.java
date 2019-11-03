package net.ndolgov.fourtytwo.queryserver.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Execute query request payload
 */
public class RequestSqlQuery {
    private final String sqlQuery;

    @JsonCreator
    public RequestSqlQuery(@JsonProperty("sqlQuery") String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestSqlQuery that = (RequestSqlQuery) o;
        return sqlQuery.equals(that.sqlQuery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sqlQuery);
    }
}
