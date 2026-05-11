package com.das.euskadimov.data.remote;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloClient;
import com.apollographql.java.rx3.Rx3Apollo;
import com.graphql.WalkingTripQuery;

import io.reactivex.rxjava3.core.Single;

public class OtpClient {
    private static final String TAG = "GraphQlClient";
    private static final OtpClient instance = new OtpClient();
    private ApolloClient client;


    private OtpClient() {
        client = new ApolloClient.Builder()
                .serverUrl("https://desarrolloavanzadosoftware.duckdns.org/otp/transmodel/v3")
                .build();
    }

    public static OtpClient getInstance() {
        return instance;
    }

    public Single<ApolloResponse<WalkingTripQuery.Data>> queryWalking(double latStart, double lonStart, double latEnd, double lonEnd) {
        var query = client.query(new WalkingTripQuery(latStart, lonStart, latEnd, lonEnd));
        return Rx3Apollo.single(query);
    }

}
