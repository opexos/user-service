package com.opexos.userservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@ApiModel("User")
public class UserDTO {

    @ApiModelProperty("Identifier")
    private Long userId;

    @ApiModelProperty("Username")
    private String username;

    @ApiModelProperty("E-mail")
    private String email;

    @ApiModelProperty("Full name")
    private String fullName;

    @ApiModelProperty("Birthday")
    private LocalDate birthday;

    @ApiModelProperty("Sex")
    private Sex sex;

}
