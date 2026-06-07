package com.ndd.digitallibrary.repository;

import com.ndd.digitallibrary.entity.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    boolean existsByUserIdAndDocumentId(Long userId, Long documentId);

    Optional<AccessLog> findByUserIdAndDocumentId(Long userId, Long documentId);

    Page<AccessLog> findByUserIdOrderByLastReadAtDesc(Long userId, Pageable pageable);
}
