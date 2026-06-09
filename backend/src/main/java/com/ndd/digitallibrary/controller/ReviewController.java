package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.request.ReviewRequest;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.dto.response.ReviewResponse;
import com.ndd.digitallibrary.entity.Review;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/documents/{documentId}/review")
    public ResponseEntity<ApiResponse<ReviewResponse>> postReview(
            @AuthenticationPrincipal User user,
            @PathVariable Long documentId,
            @Valid @RequestBody ReviewRequest request
    ){

        ApiResponse<ReviewResponse> apiResponse = ApiResponse.<ReviewResponse>builder()
                .status(200)
                .data(reviewService.createReview(documentId, user, request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user
    ){
        ApiResponse<ReviewResponse> apiResponse = ApiResponse.<ReviewResponse>builder()
                .status(200)
                .data(reviewService.updateReview(id, request, user))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/documents/{documentId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getAllReviewsByDocumentId(
            @PathVariable Long documentId,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Số trang không được nhỏ hơn 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Kích thước trang không được nhỏ hơn 1") int size
    ){
        Pageable pageable = PageRequest.of(page, size);

        ApiResponse<Page<ReviewResponse>> apiResponse = ApiResponse.<Page<ReviewResponse>>builder()
                .status(200)
                .data(reviewService.getAllReviews(documentId, pageable))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
