package com.opexos.userservice.service.user;

import com.opexos.userservice.common.Pagination;
import com.opexos.userservice.service.user.dto.UserDTO;
import com.opexos.userservice.service.user.dto.UserEditDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@Api(tags = "User service")
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;


    @ApiOperation("Search users")
    @GetMapping
    public Page<UserDTO> getUsers(UserFilter filter,
                                  @Valid Pagination pagination) {
        return userService.getUsers(filter, pagination);
    }

    @ApiOperation(value = "Create new user", notes = "You need to send a 'multipart/form-data' message " +
            "containing two parts with names: 'json' and 'photo'. Description of json fields search in the model " +
            "'User (edit)'")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDTO createUser(@RequestPart(value = "json") @Valid UserEditDTO request,
                              @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        return userService.createUser(request, photo == null ? null : photo.getBytes());
    }

    @ApiOperation(value = "Update existing user", notes = "You need to send a 'multipart/form-data' message " +
            "containing two parts with names: 'json' and 'photo'. Description of json fields search in the model " +
            "'User (edit)'")
    @PutMapping(value = "/{userId:\\d+}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserDTO updateUser(@PathVariable @ApiParam("User identifier") long userId,
                              @RequestPart(value = "json") @Valid UserEditDTO request,
                              @RequestPart(value = "photo", required = false) MultipartFile photo) throws IOException {
        return userService.updateUser(userId, request, photo == null ? null : photo.getBytes());
    }

    @ApiOperation("Returns info about user")
    @GetMapping("/{userId:\\d+}")
    public UserDTO getUser(@PathVariable @ApiParam("User identifier") long userId) {
        return userService.getUser(userId);
    }

    @ApiOperation("Returns user photo")
    @GetMapping(value = "/{userId:\\d+}/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable @ApiParam("User identifier") long userId) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore())
                .body(userService.getUserPhoto(userId));
    }

    @ApiOperation("Delete user")
    @DeleteMapping("/{userId:\\d+}")
    public void deleteUser(@PathVariable @ApiParam("User identifier") long userId) {
        userService.deleteUser(userId);
    }

}
