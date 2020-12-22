package com.opexos.userservice;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Data
@Document(indexName = "users")
class ElUser {
    @Id
    private Long userId;

    @Field(type = FieldType.Keyword)
    private String fullName;

}