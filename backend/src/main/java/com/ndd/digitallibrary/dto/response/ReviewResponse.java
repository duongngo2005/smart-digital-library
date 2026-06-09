package com.ndd.digitallibrary.dto.response;


import com.ndd.digitallibrary.entity.Review;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private short rating;
    private String comment;

    private Long reviewerId;
    private String reviewerName;
    private String reviewerAvatar;

    public static ReviewResponse fromEntity(Review review){
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewerId(review.getUser().getId())
                .reviewerName(review.getUser().getFullName())
                .reviewerAvatar(review.getUser().getAvatarUrl())
                .build();
    }
}
