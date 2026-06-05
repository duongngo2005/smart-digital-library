package com.ndd.digitallibrary.dto.response;

import com.ndd.digitallibrary.entity.Tag;
import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {
    private Long id;
    private String name;
    private String slug;

    public static TagResponse fromEntity(Tag tag){
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }
}
