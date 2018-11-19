package com.poppulo.chocolate;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;

public class GetWinnerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    final DynamoDB dynamoDb = new DynamoDB(AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("eu-west-1")
            .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
            .build());

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String game = requestEvent.getQueryStringParameters().get("game");
        QuerySpec query = new QuerySpec().withHashKey("game", game).withMaxResultSize(1);
        String winner = dynamoDb.getTable("lotto").query(query).iterator().next().getString("ticketId");
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withBody(winner);
        return responseEvent;
    }
}
