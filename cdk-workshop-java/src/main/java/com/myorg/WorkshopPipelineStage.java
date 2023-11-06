package com.myorg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Stage;
import software.amazon.awscdk.StageProps;
import software.constructs.Construct;

public class WorkshopPipelineStage extends Stage {

    public WorkshopPipelineStage(@NotNull Construct scope, @NotNull String id) {
        this(scope, id, null);
    }
    public WorkshopPipelineStage(@NotNull Construct scope, @NotNull String id, @Nullable StageProps props) {
        super(scope, id, props);

        new CdkWorkshopJavaStack(this, "WebService");
    }
}
