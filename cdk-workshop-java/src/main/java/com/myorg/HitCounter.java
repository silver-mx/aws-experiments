package com.myorg;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Map;

public class HitCounter extends Construct {
    private final Function handler;
    private final Table table;

    public HitCounter(@NotNull Construct scope, @NotNull String id, final HitCounterProps props) {
        super(scope, id);

        Attribute partitionKey = Attribute.builder()
                .name("path")
                .type(AttributeType.STRING)
                .build();
        this.table = Table.Builder.create(this, "Hits")
                .partitionKey(partitionKey)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        Map<String, String> environment = Map.of(
                "DOWNSTREAM_FUNCTION_NAME", props.getDownstream().getFunctionName(),
                "HITS_TABLE_NAME", this.table.getTableName());

        this.handler = Function.Builder.create(this, "HitCounterHandler")
                .runtime(Runtime.NODEJS_16_X)
                .handler("hitcounter.handler")
                .code(Code.fromAsset("lambda"))
                .environment(environment)
                .build();

        // Give the lambda permissions to read-write to DynamoDB
        this.table.grantReadWriteData(this.handler);

        // Give the lambda permissions to invoke another lambda
        props.getDownstream().grantInvoke(this.handler);
    }

    public Function getHandler() {
        return handler;
    }

    public Table getTable() {
        return table;
    }
}
