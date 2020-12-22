package com.opexos.userservice.service.user.elastic;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Data
@Document(indexName = "users")
public class ElUser {
    @Id
    private Long userId;

    @Field(type = FieldType.Text)
    private String fullName;

}