package com.ndd.digitallibrary.repository;

import com.ndd.digitallibrary.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserIdAndDocumentId(Long userId, Long documentId);

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByDocumentId(Long documentId,
                                  Pageable pageable);
}
