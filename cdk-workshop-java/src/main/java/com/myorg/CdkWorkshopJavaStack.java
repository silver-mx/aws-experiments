package com.myorg;

import io.github.cdklabs.dynamotableviewer.TableViewer;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class CdkWorkshopJavaStack extends Stack {

    public CdkWorkshopJavaStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public CdkWorkshopJavaStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // HelloLambda logic
        Function helloLambda = Function.Builder.create(this, "HelloHandler")
                .runtime(Runtime.NODEJS_16_X)
                .code(Code.fromAsset("lambda"))
                .handler("hello.handler")
                .build();

        // HelloLambda + HitCounter logic
        HitCounter hitCounterLambda = new HitCounter(this, "HelloHitCounter", HitCounterProps.builder()
                .downstream(helloLambda)
                .build());

        LambdaRestApi apiGateway = LambdaRestApi.Builder.create(this, "Endpoint")
                //.proxy(false) // use true to allow any request to call the lambda function
                .handler(hitCounterLambda.getHandler())
                .build();

        /*apiGateway.getRoot()
                .addResource("call-lambda")
                .addMethod("POST");*/

        // Defines a viewer (HTML) for the HitCounts table
        TableViewer.Builder.create(this, "ViewerHitCount")
                .title("Hello Hits")
                .sortBy("-hits")
                .table(hitCounterLambda.getTable())
                .build();
    }

}
