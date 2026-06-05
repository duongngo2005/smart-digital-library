package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.request.CreateCategoryRequest;
import com.ndd.digitallibrary.dto.request.UpdateCategoryRequest;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.dto.response.CategoryResponse;
import com.ndd.digitallibrary.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request){

        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .status(201)
                .message("Tạo mới danh mục thành công")
                .data(categoryService.createCategory(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){

        ApiResponse<List<CategoryResponse>> apiResponse = ApiResponse.<List<CategoryResponse>>builder()
                .data(categoryService.getAllCategories())
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id){

        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .data(categoryService.getCategoryById(id))
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryById(@PathVariable Long id){

        categoryService.deleteCategory(id);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(200)
                .message("Đã xóa danh mục thành công")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory( @Valid @RequestBody UpdateCategoryRequest request,
            @PathVariable Long id){

        ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder()
                .status(200)
                .message("Cập nhật danh mục thành công")
                .data(categoryService.updateCategory(request, id))
                .build();

        return ResponseEntity.status(200).body(apiResponse);
    }
}
