package com.ndd.digitallibrary.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Không được để trống số sao đánh giá")
    @Min(value = 1, message = "Đánh giá thấp nhất là 1 sao")
    @Max(value = 5, message = "Đánh giá cao nhất là 5 sao")
    @Builder.Default
    private Integer rating = 5;

    @Column(length = 1000, name = "Comment không được vượt quá 1000 ký tự")
    private String comment;
}
