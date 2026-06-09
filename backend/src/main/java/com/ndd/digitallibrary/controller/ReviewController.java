package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.request.ReviewRequest;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.dto.response.ReviewResponse;
import com.ndd.digitallibrary.entity.Review;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
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

}
