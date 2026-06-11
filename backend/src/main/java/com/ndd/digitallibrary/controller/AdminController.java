package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.request.UpdateUserStatusRequest;
import com.ndd.digitallibrary.dto.request.UserFilterRequest;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.dto.response.UserResponse;
import com.ndd.digitallibrary.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @ModelAttribute UserFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        ApiResponse<Page<UserResponse>> apiResponse = ApiResponse.<Page<UserResponse>>builder()
                .status(200)
                .data(adminService.searchUser(filterRequest, pageable))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @Valid @RequestBody UpdateUserStatusRequest request,
            @PathVariable Long id
    ){
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .message("Thay đổi trạng thái người dùng thành công")
                .status(200)
                .data(adminService.updateUserStatus(request, id))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
