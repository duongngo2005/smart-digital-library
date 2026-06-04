package com.ndd.digitallibrary.repository;

import com.ndd.digitallibrary.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
