package com.ndd.digitallibrary.controller;

import com.cloudinary.Api;
import com.ndd.digitallibrary.dto.request.TagRequest;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.dto.response.TagResponse;
import com.ndd.digitallibrary.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tags")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<TagResponse>> createTag(@Valid @RequestBody TagRequest request){

        ApiResponse<TagResponse> apiResponse = ApiResponse.<TagResponse>builder()
                .status(201)
                .message("Tạo thẻ thành công")
                .data(tagService.createTag(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request){

        ApiResponse<TagResponse> apiResponse = ApiResponse.<TagResponse>builder()
                .status(200)
                .message("Cập nhật thẻ thành công")
                .data(tagService.updateTag(request, id))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteTagById(@PathVariable Long id){

        tagService.deleteTag(id);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(200)
                .message("Xóa thẻ thành công")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags(){

        ApiResponse<List<TagResponse>> apiResponse = ApiResponse.<List<TagResponse>>builder()
                .status(200)
                .data(tagService.getAllTag())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> getTagById(@PathVariable Long id){

        ApiResponse<TagResponse> apiResponse = ApiResponse.<TagResponse>builder()
                .status(200)
                .data(tagService.getTagById(id))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}
