package com.ndd.digitallibrary.entity;

import com.ndd.digitallibrary.enums.DocumentStatus;
import com.ndd.digitallibrary.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "documents")
@Setter @Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    private String publisher;
    private Integer publishedYear;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String coverUrl;
    @Column(nullable = false)
    private String fileUrl;
    @Column(nullable = false)
    private String cloudinaryPublicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;
    private BigDecimal fileSizeMb;

    @Builder.Default
    @Column(name = "is_public")
    private boolean publicAccess = false;

    @Builder.Default
    private Long viewCount = 0L;
    @Builder.Default
    private Long downloadCount = 0L;
    @Builder.Default
    private Long reviewCount = 0L;
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DocumentStatus documentStatus = DocumentStatus.DRAFT;

    @JoinColumn(name = "uploaded_by")
    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
}
