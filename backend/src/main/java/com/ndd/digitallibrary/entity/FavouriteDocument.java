package com.ndd.digitallibrary.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favourite_documents", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "document_id"})
})
public class FavouriteDocument extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "document_id")
    private Document document;
}
