package com.ndd.digitallibrary.repository;

import com.ndd.digitallibrary.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
