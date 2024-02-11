package com.github.dmh.spark;

import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

public final class Context {
    private final LogicalPlan plan;

    public Context(LogicalPlan plan) {
        this.plan = plan;
    }
}
