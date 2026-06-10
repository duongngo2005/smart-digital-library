package com.ndd.digitallibrary.specification;

import com.ndd.digitallibrary.entity.Document;
import com.ndd.digitallibrary.entity.Tag;
import com.ndd.digitallibrary.enums.DocumentStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;


public class DocumentSpecification {
    private DocumentSpecification(){}

    public static Specification<Document> hasKeyword(String keyword){
        return (root, query, cb) -> {
            if(keyword == null || keyword.isBlank()){
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("author")), pattern)
            );
        };
    }

    public static Specification<Document> hasCategoryId(Long categoryId){
        return(root, query, cb) -> {
            if(categoryId == null){
                return cb.conjunction();
            }

            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Document> hasTag(Long tagId){
        return (root, query, cb) -> {
            if(tagId == null){
                return cb.conjunction();
            }

            query.distinct(true);

            Join<Document, Tag> tagJoin = root.join("tags");
            return cb.equal(tagJoin.get("id"), tagId);
        };
    }

    public static Specification<Document> isPublished(){
        return (root, query, cb) -> {
            return cb.equal(root.get("documentStatus"), DocumentStatus.PUBLISHED);
        };
    }
}
