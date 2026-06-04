package com.ndd.digitallibrary.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UpdateCategoryRequest {
    private String name;
    private Long parentId;
    private String description;
}
