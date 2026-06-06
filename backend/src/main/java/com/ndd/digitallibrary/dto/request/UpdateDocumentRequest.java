package com.ndd.digitallibrary.dto.request;

import com.ndd.digitallibrary.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
public class UpdateDocumentRequest {

    private String title;
    private String author;
    private String publisher;
    private Integer publishedYear;
    private String description;

    private MultipartFile coverFile;
    private MultipartFile documentFile;

    private Boolean publicAccess;
    private DocumentStatus documentStatus;

    private List<String> tagNames;

    private Long categoryId;
}