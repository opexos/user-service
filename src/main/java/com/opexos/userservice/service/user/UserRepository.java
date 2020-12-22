package com.opexos.userservice.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    int countByEmail(String email);

    int countByEmailAndUserIdNot(String email, long userId);

    int countByUsername(String username);

    int countByUsernameAndUserIdNot(String username, long userId);

    <T> List<T> findAllByUserIdIn(List<Long> ids, Class<T> type);

    <T> Page<T> findBy(Pageable pageable, Class<T> type);
}
