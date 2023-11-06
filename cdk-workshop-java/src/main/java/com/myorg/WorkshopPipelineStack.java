package com.myorg;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.pipelines.CodeBuildStep;
import software.amazon.awscdk.pipelines.CodePipeline;
import software.amazon.awscdk.pipelines.CodePipelineSource;
import software.amazon.awscdk.pipelines.StageDeployment;
import software.amazon.awscdk.services.codecommit.Code;
import software.amazon.awscdk.services.codecommit.Repository;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class WorkshopPipelineStack extends Stack {

    public WorkshopPipelineStack(@Nullable Construct scope, @Nullable String id) {
        this(scope, id, null);
    }

    public WorkshopPipelineStack(@Nullable Construct scope, @Nullable String id, @Nullable StackProps props) {
        super(scope, id, props);

        Repository repo = Repository.Builder.create(this, "WorkshopRepo")
                .repositoryName("WorkshopRepo")
                .build();

        CodePipeline pipeline = CodePipeline.Builder.create(this, "Pipeline")
                .pipelineName("WorkshopPipeline")
                .synth(CodeBuildStep.Builder.create("SynthStep")
                        .input(CodePipelineSource.codeCommit(repo, "main"))
                        .installCommands(List.of(
                                "npm install -g aws-cdk"
                        ))
                        .commands(List.of(
                                "cd cdk-workshop-java",
                                "mvn package",
                                "npx cdk synth"
                        ))
                        .primaryOutputDirectory("cdk-workshop-java/cdk.out")// Required as cdk-workshop-java is a subfolder in the repo.
                        .build())
                .build();

        WorkshopPipelineStage deployStage = new WorkshopPipelineStage(this, "Deploy");
        StageDeployment stageDeployment = pipeline.addStage(deployStage);

        // Test the deployment
        stageDeployment.addPost(
                CodeBuildStep.Builder.create("TestViewerEndpoint")
                        .projectName("TestViewerEndpoint")
                        .commands(List.of("curl -Ssf $ENDPOINT_URL"))
                        .envFromCfnOutputs(Map.of("ENDPOINT_URL", deployStage.hcViewerUrl))
                        .build(),
                CodeBuildStep.Builder.create("TestAPIGatewayEndpoint")
                        .projectName("TestAPIGatewayEndpoint")
                        .envFromCfnOutputs(Map.of("ENDPOINT_URL", deployStage.hcEndpoint))
                        .commands(List.of(
                                "curl -Ssf $ENDPOINT_URL",
                                "curl -Ssf $ENDPOINT_URL/hello",
                                "curl -Ssf $ENDPOINT_URL/test"
                        ))
                        .build()
        );


        );

    }

}
