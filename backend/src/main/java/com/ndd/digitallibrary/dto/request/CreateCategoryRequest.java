package com.ndd.digitallibrary.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateCategoryRequest {
    @NotBlank(message = "Không được để trống tên chủ đề")
    private String name;
    private String description;
    private String parent;
}
