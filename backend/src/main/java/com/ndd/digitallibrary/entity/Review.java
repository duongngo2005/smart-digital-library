package com.ndd.digitallibrary.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews", uniqueConstraints={
        @UniqueConstraint(columnNames = {"user_id", "document_id"})
})
public class Review extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    private short rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
