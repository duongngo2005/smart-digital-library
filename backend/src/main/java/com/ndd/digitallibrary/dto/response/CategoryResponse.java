package com.ndd.digitallibrary.dto.response;

import com.ndd.digitallibrary.entity.Category;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private Category parent;
    private List<CategoryResponse> children;
}
