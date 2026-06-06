package com.ndd.digitallibrary.dto.request;

import com.ndd.digitallibrary.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreateDocumentRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    private String author;
    private String publisher;
    private Integer publishedYear;
    private String description;

    private DocumentStatus documentStatus;
    private Boolean publicAccess;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;

    private List<String> tagNames;

    private MultipartFile coverFile;

    @NotNull(message = "Vui lòng upload file tài liệu")
    private MultipartFile documentFile;
}
