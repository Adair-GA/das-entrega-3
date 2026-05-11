package com.das.euskadimov.data.local;

import com.graphql.WalkingTripQuery;

public class TripPatternHelper {
    private static TripPatternHelper instance = new TripPatternHelper();
    private WalkingTripQuery.TripPattern lastPattern;


    private TripPatternHelper(){}

    public static TripPatternHelper getInstance() {
        return instance;
    }

    public WalkingTripQuery.TripPattern getLastPattern() {
        return lastPattern;
    }

    public void setLastPattern(WalkingTripQuery.TripPattern lastPattern) {
        this.lastPattern = lastPattern;
    }
}
