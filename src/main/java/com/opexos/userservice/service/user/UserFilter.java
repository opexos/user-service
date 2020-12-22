package com.opexos.userservice.service.user;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class UserFilter {

    @ApiParam("Search by full name")
    private String fullName;

}
