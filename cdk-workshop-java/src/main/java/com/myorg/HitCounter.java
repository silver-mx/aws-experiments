package com.myorg;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableEncryption;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class HitCounter extends Construct {
    private final Function handler;
    private final Table table;

    public HitCounter(@NotNull Construct scope, @NotNull String id, final HitCounterProps props) {
        super(scope, id);

        if (nonNull(props.getReadCapacity())) {
            if (props.getReadCapacity().intValue() < 5 || props.getReadCapacity().intValue() > 20) {
                throw new IllegalArgumentException("readCapacity must be greater than 5 or less than 20");
            }
        }

        Attribute partitionKey = Attribute.builder()
                .name("path")
                .type(AttributeType.STRING)
                .build();

        this.table = Table.Builder.create(this, "Hits")
                .partitionKey(partitionKey)
                .encryption(TableEncryption.AWS_MANAGED)
                .readCapacity(nonNull(props.getReadCapacity()) ? props.getReadCapacity() : 5)
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
