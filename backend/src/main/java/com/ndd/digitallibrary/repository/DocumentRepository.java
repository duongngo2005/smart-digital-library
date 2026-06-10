package com.ndd.digitallibrary.repository;

import com.ndd.digitallibrary.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    boolean existsByTitleAndAuthorAndPublisherAndPublishedYear(String title, String author, String publisher, Integer publishYear);

}
