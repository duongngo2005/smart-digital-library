package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.request.CreateDocumentRequest;
import com.ndd.digitallibrary.dto.request.UpdateDocumentRequest;
import com.ndd.digitallibrary.dto.response.ApiResponse;
import com.ndd.digitallibrary.dto.response.DocumentResponse;
import com.ndd.digitallibrary.entity.Document;
import com.ndd.digitallibrary.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAllDocuments(){

        ApiResponse<List<DocumentResponse>> apiResponse = ApiResponse.<List<DocumentResponse>>builder()
                .data(documentService.getAllDocuments())
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getDocumentById(@PathVariable Long id){

        ApiResponse<DocumentResponse> apiResponse = ApiResponse.<DocumentResponse>builder()
                .data(documentService.getDocumentById(id))
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponse>> createDocument(
            @Valid @ModelAttribute CreateDocumentRequest request,
            Authentication authentication
    ){

        ApiResponse<DocumentResponse> apiResponse = ApiResponse.<DocumentResponse>builder()
                .data(documentService.createDocument(request, authentication.getName()))
                .status(201)
                .message("Tạo tài liệu thành công")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id){

        documentService.deleteDocument(id);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Xóa tài liệu thành công")
                .status(200)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping(value ="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateDocument(
            @Valid @ModelAttribute UpdateDocumentRequest request,
            @PathVariable Long id
    ){

        ApiResponse<DocumentResponse> apiResponse = ApiResponse.<DocumentResponse>builder()
                .data(documentService.updateDocument(request, id))
                .status(200)
                .message("Cập nhật tài liệu thành công")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
