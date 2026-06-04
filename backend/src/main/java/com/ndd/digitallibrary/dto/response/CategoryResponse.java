package com.ndd.digitallibrary.dto.response;

import com.ndd.digitallibrary.entity.Category;
import lombok.*;

import java.util.ArrayList;
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
    private Long parentId;
    private List<CategoryResponse> children;

    public static CategoryResponse fromEntity(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .children(
                        category.getChildren() != null
                                ? category.getChildren().stream().map(CategoryResponse::fromEntity).toList()
                                : new ArrayList<>())
                .description(category.getDescription())
                .build();
    }

}
