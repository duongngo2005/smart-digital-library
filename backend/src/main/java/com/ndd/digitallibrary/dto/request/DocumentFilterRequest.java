package com.ndd.digitallibrary.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentFilterRequest {
    private String keyword;
    private Long categoryId;
    private Long tagId;
}
