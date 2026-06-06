package com.ndd.digitallibrary.dto.response;

import com.ndd.digitallibrary.entity.Document;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long id;
    private String title;
    private String author;
    private int publishedYear;
    private String publisher;
    private String description;
    private String coverUrl;
    private String fileType;
    private BigDecimal fileSizeMb;
    private boolean publicAccess;
    private Long viewCount;
    private Long downloadCount;
    private Long reviewCount;
    private BigDecimal averageRating;
    private String documentStatus;
    private Long uploadBy;
    private Long category;

    public static DocumentResponse fromEntity(Document document){
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .author(document.getAuthor())
                .publisher(document.getPublisher())
                .publishedYear(document.getPublishedYear())
                .description(document.getDescription())
                .coverUrl(document.getCoverUrl())
                .fileSizeMb(document.getFileSizeMb())
                .fileType(document.getFileType().name())
                .publicAccess(document.isPublicAccess())
                .viewCount(document.getViewCount())
                .downloadCount(document.getDownloadCount())
                .reviewCount(document.getReviewCount())
                .averageRating(document.getAverageRating())
                .documentStatus(document.getDocumentStatus().name())
                .uploadBy(document.getUploadedBy().getId())
                .category(document.getCategory().getId())
                .build();
    }

}
