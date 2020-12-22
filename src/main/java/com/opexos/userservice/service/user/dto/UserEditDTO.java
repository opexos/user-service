package com.opexos.userservice.service.user.dto;

import com.opexos.userservice.service.user.PhotoAction;
import com.opexos.userservice.service.user.Sex;
import com.opexos.userservice.service.user.validation.UserValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@ApiModel("User (edit)")
@UserValidator
public class UserEditDTO {

    @ApiModelProperty("Username")
    private String username;

    @ApiModelProperty("E-mail")
    //@javax.validation.constraints.Email checks poorly
    @Pattern(regexp = "^[a-zA-Z0-9_\\-.]+@[a-zA-Z0-9_\\-.]+\\.[a-zA-Z]{2,5}$",
            message = "{user.validation.email-incorrect}")
    private String email;

    @ApiModelProperty(value = "Full name", required = true)
    @NotBlank(message = "{user.validation.fullname-mandatory}")
    private String fullName;

    @ApiModelProperty("Birthday")
    private LocalDate birthday;

    @ApiModelProperty("Sex")
    private Sex sex;

    @ApiModelProperty(value = "Action with a photo", required = true,
            notes = "If you specify that you need to upload a photo, then the multipart " +
                    "message must contain a part named 'photo'")
    @NotNull
    private PhotoAction photoAction;

}
