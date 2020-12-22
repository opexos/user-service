package com.opexos.userservice.service.user.elastic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElUserRepository extends ElasticsearchRepository<ElUser, Long> {
    Page<ElUser> findByFullNameLike(String fullName, Pageable pageable);
}
