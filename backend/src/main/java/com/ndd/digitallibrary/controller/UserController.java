package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.request.ChangePasswordRequest;
import com.ndd.digitallibrary.dto.request.UpdateProfileRequest;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.dto.response.UserResponse;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(@AuthenticationPrincipal User user){

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .status(200)
                .data(userService.getMyUserProfile(user))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User user
    ){
        Long userId = user.getId();

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .status(200)
                .data(userService.updateProfile(userId, request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ){
        Long userId = user.getId();

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .status(200)
                .data(userService.updateAvatar(userId, file))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request
    ){
        Long userId = user.getId();

        userService.changePassword(userId, request);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(200)
                .message("Đổi mật khẩu thành công")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
