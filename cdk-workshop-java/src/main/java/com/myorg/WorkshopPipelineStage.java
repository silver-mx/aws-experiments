package com.myorg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stage;
import software.amazon.awscdk.StageProps;
import software.constructs.Construct;

public class WorkshopPipelineStage extends Stage {

    public final CfnOutput hcViewerUrl;
    public final CfnOutput hcEndpoint;

    public WorkshopPipelineStage(@NotNull Construct scope, @NotNull String id) {
        this(scope, id, null);
    }
    public WorkshopPipelineStage(@NotNull Construct scope, @NotNull String id, @Nullable StageProps props) {
        super(scope, id, props);

        CdkWorkshopJavaStack webService = new CdkWorkshopJavaStack(this, "WebService");

        hcViewerUrl = webService.hcViewerUrl;
        hcEndpoint = webService.hcEndpoint;
    }
}
