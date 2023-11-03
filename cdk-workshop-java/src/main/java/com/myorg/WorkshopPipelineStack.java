package com.myorg;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.codecommit.Repository;
import software.constructs.Construct;

public class WorkshopPipelineStack extends Stack {

    public WorkshopPipelineStack(@Nullable Construct scope, @Nullable String id) {
        this(scope, id, null);
    }

    public WorkshopPipelineStack(@Nullable Construct scope, @Nullable String id, @Nullable StackProps props) {
        super(scope, id, props);

        Repository repo = Repository.Builder.create(this, "WorkshopRepo")
                .repositoryName("WorkshopRepo")
                .build();
    }

}
