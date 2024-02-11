package com.github.dmh.spark.agent;

import org.apache.spark.sql.execution.QueryExecution;
import org.apache.spark.sql.util.QueryExecutionListener;
import com.github.dmh.spark.Context;

public final class Listener implements QueryExecutionListener {
    @Override
    public void onSuccess(String funcName, QueryExecution qe, long durationNs) {
        var context = new Context(qe.analyzed());
    }

    @Override
    public void onFailure(String funcName, QueryExecution qe, Exception exception) {}
}
