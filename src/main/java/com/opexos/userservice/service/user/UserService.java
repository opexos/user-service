package com.opexos.userservice.service.user;

import com.opexos.userservice.common.Pagination;
import com.opexos.userservice.configuration.UserServiceProperties;
import com.opexos.userservice.exception.BadRequestException;
import com.opexos.userservice.exception.InvalidImageException;
import com.opexos.userservice.exception.NotFoundException;
import com.opexos.userservice.service.image.ImageService;
import com.opexos.userservice.service.photo.PhotoService;
import com.opexos.userservice.service.user.dto.UserDTO;
import com.opexos.userservice.service.user.dto.UserEditDTO;
import com.opexos.userservice.service.user.elastic.ElUser;
import com.opexos.userservice.service.user.elastic.ElUserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserServiceProperties properties;
    private final ImageService imageService;
    private final MessageSourceAccessor message;
    private final PhotoService photoService;
    private final ElUserRepository elUserRepository;

    @Transactional
    public UserDTO createUser(@NonNull UserEditDTO dto, byte[] photo) {
        return saveUser(new User(), dto, photo);
    }

    @Transactional
    public UserDTO updateUser(long userId, @NonNull UserEditDTO dto, byte[] photo) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        return saveUser(user, dto, photo);
    }

    @Transactional
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException();
        }
        userRepository.deleteById(userId);
        elUserRepository.deleteById(userId);
        photoService.deletePhoto(userId);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        return userMapper.toDTO(user);
    }


    @Transactional(readOnly = true)
    public Page<UserDTO> getUsers(@NonNull UserFilter filter,
                                  @NonNull Pagination pagination) {

        if (filter.getFullName() != null) {
            PageRequest pageable = PageRequest.of(
                    pagination.getPageNum(),
                    pagination.getPageSize(),
                    Sort.sort(ElUser.class).by(ElUser::getUserId));

            //search users in elastic by full name
            Page<ElUser> users = elUserRepository.findByFullNameLike(filter.getFullName(), pageable);

            //get data from db
            List<Long> userIds = users.getContent().stream().map(ElUser::getUserId).collect(Collectors.toList());
            List<UserDTO> usersDb = userRepository.findAllByUserIdIn(userIds, UserListProjection.class)
                    .stream()
                    .sorted(Comparator.comparingLong(UserListProjection::getUserId))
                    .map(userMapper::toDTO).collect(Collectors.toList());

            //return result
            return new PageImpl<>(usersDb, users.getPageable(), users.getTotalElements());
        }


        Pageable pageable = PageRequest.of(
                pagination.getPageNum(),
                pagination.getPageSize(),
                Sort.sort(User.class).by(User::getUserId));

        return userRepository.findBy(pageable, UserListProjection.class).map(userMapper::toDTO);
    }

    public byte[] getUserPhoto(long userId) {
        byte[] photo = photoService.getPhoto(userId);
        if (photo == null) {
            throw new NotFoundException();
        }
        return photo;
    }

    private UserDTO saveUser(User user, UserEditDTO dto, byte[] photo) {
        if (PhotoAction.UPLOAD.equals(dto.getPhotoAction())) {
            if (photo == null || photo.length == 0) {
                throw new BadRequestException(message.getMessage("user.validation.incorrect-photo"));
            }

            //make cropped photo
            try {
                photo = imageService.getCroppedImage(photo, properties.getUserPhotoSideSize());
            } catch (InvalidImageException e) {
                throw new BadRequestException(message.getMessage("user.validation.incorrect-photo"));
            }
        }

        userMapper.fill(user, dto);
        userRepository.save(user);

        elUserRepository.save(ElUser.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .build());


        if (PhotoAction.UPLOAD.equals(dto.getPhotoAction())) {
            photoService.savePhoto(user.getUserId(), photo);
        } else if (PhotoAction.DELETE.equals(dto.getPhotoAction())) {
            photoService.deletePhoto(user.getUserId());
        }

        return userMapper.toDTO(user);
    }
}
