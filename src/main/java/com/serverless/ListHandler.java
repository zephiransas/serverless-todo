package com.serverless;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListHandler implements RequestHandler<Object, ApiGatewayResponse> {

    @Override
    public ApiGatewayResponse handleRequest(Object input, Context context) {
        List<Todo> todoStream = scan();

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(todoStream)
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }

    private List<Todo> scan() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .build();

        String tableName = System.getenv("DYNAMODB_TABLE");
        ScanRequest request = new ScanRequest()
                .withTableName(tableName);

        ScanResult scanResult = client.scan(request);
        return scanResult.getItems()
                .stream()
                .map(result -> {
                    Todo todo = new Todo();
                    todo.setId(result.get("id").getS());
                    todo.setBody(result.get("body").getS());
                    todo.setDone(result.get("done").getBOOL());
                    return todo;
                })
                .collect(Collectors.toList());
    }
}
