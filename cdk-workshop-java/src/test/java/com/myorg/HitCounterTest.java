package com.myorg;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.assertions.Capture;
import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.amazon.awscdk.services.lambda.Runtime.NODEJS_16_X;

public class HitCounterTest {

    @Test
    public void testDynamoDBTable() {
        Template template = createHitCounterAndSynthetizeToTemplate();
        template.resourceCountIs("AWS::DynamoDB::Table", 1);
    }

    @Test
    public void testLambdaEnvVars() {
        Template template = createHitCounterAndSynthetizeToTemplate();
        Capture envCapture = new Capture();
        Map<String, Object> expected = Map.of(
                "Handler", "hitcounter.handler",
                "Environment", envCapture);

        template.hasResourceProperties("AWS::Lambda::Function", expected);

        Map<String, Object> expectedEnv = Map.of(
                "Variables", Map.of(
                        "DOWNSTREAM_FUNCTION_NAME", Map.of("Ref", "HelloHandler2E4FBA4D"),
                        "HITS_TABLE_NAME", Map.of("Ref", "HelloHitCounterHits7AAEBF80")
                ));
        assertThat(envCapture.asObject()).isEqualTo(expectedEnv);
    }

    @Test
    public void testDynamoDBEncryption() {
        Template template = createHitCounterAndSynthetizeToTemplate();
        ;
        Map<String, Object> expected = Map.of(
                "SSESpecification", Map.of("SSEEnabled", true));

        template.hasResourceProperties("AWS::DynamoDB::Table", expected);
    }

    @Test
    public void testDynamoDBRaises() {
        Stack stack = new Stack();

        Function hello = createHelloLambdaFunction(stack);

        assertThrows(IllegalArgumentException.class, () -> {
            new HitCounter(stack, "HelloHitCounter", HitCounterProps.builder()
                    .downstream(hello)
                    .readCapacity(1)
                    .build());
        });
    }

    @NotNull
    private static Template createHitCounterAndSynthetizeToTemplate() {
        Stack stack = new Stack();

        Function hello = createHelloLambdaFunction(stack);

        new HitCounter(stack, "HelloHitCounter", HitCounterProps.builder()
                .downstream(hello)
                .build());

        return Template.fromStack(stack);
    }

    @NotNull
    private static Function createHelloLambdaFunction(Stack stack) {
        return Function.Builder.create(stack, "HelloHandler")
                .runtime(NODEJS_16_X)
                .code(Code.fromAsset("lambda"))
                .handler("hello.handler")
                .build();
    }

}