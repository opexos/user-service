package com.opexos.userservice.service.user.validation;

import com.opexos.userservice.service.user.UserRepository;
import com.opexos.userservice.service.user.dto.UserEditDTO;
import com.opexos.userservice.validation.AbstractValidator;
import com.opexos.userservice.validation.ConstraintValidatorContextWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidatorImpl extends AbstractValidator<UserValidator, UserEditDTO> {

    private final UserRepository userRepository;

    @Override
    protected void doValidation(UserEditDTO value, ConstraintValidatorContextWrapper context) {
        HttpMethod method = getRequestMethod();

        //check the uniqueness of the user name
        if (value.getUsername() != null) {

            //store user names in lowercase for easier verification of uniqueness
            value.setUsername(value.getUsername().toLowerCase());

            if ((HttpMethod.POST.equals(method) && userRepository.countByUsername(value.getUsername()) > 0)
                    || (HttpMethod.PUT.equals(method) && userRepository.countByUsernameAndUserIdNot(
                    value.getUsername(), getRequestPathVariable("userId")) > 0)) {
                context.addConstraintViolation("username", "{user.validation.username-exists}");
            }
        }

        //check the uniqueness of the e-mail
        if (value.getEmail() != null) {

            //store e-mails in lowercase for easier verification of uniqueness
            value.setEmail(value.getEmail().toLowerCase());

            if ((HttpMethod.POST.equals(method) && userRepository.countByEmail(value.getEmail()) > 0)
                    || (HttpMethod.PUT.equals(method) && userRepository.countByEmailAndUserIdNot(
                    value.getEmail(), getRequestPathVariable("userId")) > 0)) {
                context.addConstraintViolation("email", "{user.validation.email-exists}");
            }
        }

    }
}
