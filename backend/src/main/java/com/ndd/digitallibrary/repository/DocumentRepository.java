package com.ndd.digitallibrary.repository;

import com.ndd.digitallibrary.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
