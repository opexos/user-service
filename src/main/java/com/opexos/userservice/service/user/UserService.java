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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    private final ElasticsearchOperations elasticsearchOperations;

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
        elasticsearchOperations.delete(String.valueOf(userId), ElUser.class);
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

        elasticSearch:
        if (filter.getFullName() != null) {
            //delete all digits and special characters
            String fullName = filter.getFullName()
                    .replaceAll("[\\d`~!@#$%^&*()_=+{}\\\\|\\[\\]?<>,./;:\"'-]", "")
                    .trim();

            if (fullName.isEmpty()) {
                //no string to search
                break elasticSearch;
            }

            Pageable pageable = PageRequest.of(
                    pagination.getPageNum(),
                    pagination.getPageSize(),
                    Sort.sort(ElUser.class).by(ElUser::getUserId));

            Criteria criteria = Arrays.stream(fullName.split(" "))
                    .map(String::trim)
                    .filter(it -> !it.isEmpty())
                    .reduce(new Criteria(),
                            (cr, str) -> cr.and("fullName").contains(str),
                            Criteria::and);

            //unfortunately we can't build a complex query with pagination using repository
            SearchHits<ElUser> hits = elasticsearchOperations.search(
                    new CriteriaQuery(criteria, pageable), ElUser.class);

            //collect user ids
            List<Long> userIds = hits.getSearchHits().stream()
                    .map(it -> it.getContent().getUserId())
                    .collect(Collectors.toList());

            //get data from db
            List<UserDTO> usersDb = userRepository.findAllByUserIdIn(userIds, UserListProjection.class)
                    .stream()
                    .sorted(Comparator.comparingLong(UserListProjection::getUserId))
                    .map(userMapper::toDTO).collect(Collectors.toList());

            //return result
            return new PageImpl<>(usersDb, pageable, hits.getTotalHits());
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

        elasticsearchOperations.save(ElUser.builder()
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
