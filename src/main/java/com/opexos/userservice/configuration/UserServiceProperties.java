package com.opexos.userservice.configuration;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Component
@ConfigurationProperties("user-service")
@Validated
public class UserServiceProperties {

    /**
     * Size of the side of the user's photo in pixels to save to the database (default 200).
     */
    @Range(min = 100, max = 500)
    private int userPhotoSideSize = 200;

    /**
     * Name of the folder for saving user photos (default 'photo').
     */
    @NotBlank
    private String photoFolder = "photo";

}
