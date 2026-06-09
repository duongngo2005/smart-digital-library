package com.ndd.digitallibrary.dto.response;


import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Integer rating;
    private String comment;

    private Long reviewerId;
    private String reviewName;
    private String reviewerAvatar;

}
