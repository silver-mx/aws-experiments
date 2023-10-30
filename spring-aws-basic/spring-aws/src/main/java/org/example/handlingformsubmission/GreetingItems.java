package org.example.handlingformsubmission;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class GreetingItems {

    private String id;
    private String name;
    private String message;
    private String title;

    public GreetingItems() {
    }

    @DynamoDbPartitionKey
    public void setId(String id) {
        this.id = id;
    }
}
