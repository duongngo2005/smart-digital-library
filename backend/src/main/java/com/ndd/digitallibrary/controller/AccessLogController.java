package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.response.AccessLogResponse;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/access-log")
public class AccessLogController {

    private final AccessLogService accessLogService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<AccessLogResponse>>> getUserReadingHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){

        Pageable pageable = PageRequest.of(page, size);

        User user = (User) authentication.getPrincipal();

        ApiResponse<Page<AccessLogResponse>> apiResponse = ApiResponse.<Page<AccessLogResponse>>builder()
                .data(accessLogService.getUserReadingHistory(user.getId(), pageable))
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/documents/{documentId}/progress")
    public ResponseEntity<ApiResponse<Void>> updateReadingProgress(
            @PathVariable Long documentId,
            Authentication authentication,
            @RequestParam int page
    ){

        User user = (User) authentication.getPrincipal();

        accessLogService.updateReadingProgress(user.getId(), documentId, page);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
