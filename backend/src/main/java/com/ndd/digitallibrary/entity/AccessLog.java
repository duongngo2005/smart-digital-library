package com.ndd.digitallibrary.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "access_logs", uniqueConstraints={
        @UniqueConstraint(columnNames = {"user_id", "document_id"})
})
public class AccessLog extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false)
    private LocalDateTime lastReadAt = LocalDateTime.now();

    @Builder.Default
    private boolean hasDownloaded = false;

    @Builder.Default
    private int lastReadPage = 1;
}
