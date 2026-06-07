package com.ndd.digitallibrary.dto.response;

import com.ndd.digitallibrary.entity.Document;
import com.ndd.digitallibrary.enums.FileType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DocumentSummaryResponse {

    private Long id;
    private String title;
    private String author;

    private String coverUrl;
    private FileType fileType;
    private Boolean publicAccess;

    private BigDecimal averageRating;
    private Long viewCount;

    public static DocumentSummaryResponse fromEntity(Document document){
        return DocumentSummaryResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .author(document.getAuthor())
                .coverUrl(document.getCoverUrl())
                .fileType(document.getFileType())
                .publicAccess(document.isPublicAccess())
                .averageRating(document.getAverageRating())
                .viewCount(document.getViewCount())
                .build();
    }
}
