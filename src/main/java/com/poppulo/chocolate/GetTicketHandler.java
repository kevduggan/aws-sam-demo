package com.poppulo.chocolate;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;

import java.util.UUID;

public class GetTicketHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    final DynamoDB dynamoDb = new DynamoDB(AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("eu-west-1")
            .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
            .build());

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String game = requestEvent.getQueryStringParameters().get("game");
        String ticketId = UUID.randomUUID().toString();
        Double ticketValue = Math.random();
        Item ticket = new Item()
                .withString("game", game)
                .withString("ticketId", ticketId)
                .withNumber("ticketValue", ticketValue);
        dynamoDb.getTable("lotto").putItem(ticket);
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withBody(ticketId);
        return responseEvent;
    }

}
