package com.ndd.digitallibrary.service;

import com.ndd.digitallibrary.dto.request.CreateDocumentRequest;
import com.ndd.digitallibrary.dto.request.UpdateDocumentRequest;
import com.ndd.digitallibrary.dto.response.CloudinaryResponse;
import com.ndd.digitallibrary.dto.response.DocumentResponse;
import com.ndd.digitallibrary.entity.Category;
import com.ndd.digitallibrary.entity.Document;
import com.ndd.digitallibrary.entity.Tag;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.enums.FileType;
import com.ndd.digitallibrary.repository.CategoryRepository;
import com.ndd.digitallibrary.repository.DocumentRepository;
import com.ndd.digitallibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    public DocumentResponse getDocumentById(Long id){

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu này"));

        return DocumentResponse.fromEntity(document);
    }

    public List<DocumentResponse> getAllDocuments(){
        return documentRepository.findAll().stream().map(DocumentResponse::fromEntity).toList();
    }

    @Transactional
    public DocumentResponse createDocument(CreateDocumentRequest request, String userEmail){

        User uploader = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));

        CloudinaryResponse fileResponse = cloudinaryService.uploadDocument(request.getDocumentFile());

        double mb = request.getDocumentFile().getSize() / (1024.0 * 1024.0);
        BigDecimal fileSizeMb = BigDecimal.valueOf(Math.round(mb * 100.0) / 100.0);

        String originalFileName = request.getDocumentFile().getOriginalFilename();
        String fileType = "UNKNOWN";
        if(originalFileName != null && originalFileName.contains(".")){
            fileType = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toUpperCase();
        }

        String coverUrl = null;
        String coverPublicId = null;
        if(request.getCoverFile() != null && !request.getCoverFile().isEmpty()){
            CloudinaryResponse coverResponse = cloudinaryService.uploadCover(request.getCoverFile());
            coverPublicId = coverResponse.getPublicId();
            coverUrl = coverResponse.getUrl();
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        List<Tag> tags = new ArrayList<>();
        if(request.getTagNames() != null && !request.getTagNames().isEmpty()){
            tags = tagService.findOrCreateTag(request.getTagNames());
        }

        Document.DocumentBuilder documentBuilder = Document.builder()
                .fileUrl(fileResponse.getUrl())
                .coverUrl(coverUrl)
                .author(request.getAuthor())
                .description(request.getDescription())
                .category(category)
                .tags(tags)
                .title(request.getTitle())
                .fileSizeMb(fileSizeMb)
                .fileType(FileType.valueOf(fileType))
                .publisher(request.getPublisher())
                .publishedYear(request.getPublishedYear())
                .uploadedBy(uploader)
                .filePublicId(fileResponse.getPublicId())
                .coverPublicId(coverPublicId);

        if(request.getDocumentStatus() != null){
            documentBuilder.documentStatus(request.getDocumentStatus());
        }

        if(request.getPublicAccess() != null){
            documentBuilder.publicAccess(request.getPublicAccess());
        }

        Document document = documentBuilder.build();

        document = documentRepository.save(document);

        return DocumentResponse.fromEntity(document);
    }

    @Transactional
    public void deleteDocument(Long id){

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu này"));

        cloudinaryService.deleteFile(document.getFilePublicId(), "raw");

        if (document.getCoverUrl() != null && document.getCoverPublicId() != null){
            cloudinaryService.deleteFile(document.getCoverPublicId(), "image");
        }

        documentRepository.delete(document);
    }

    @Transactional
    public DocumentResponse updateDocument(UpdateDocumentRequest request, Long id){

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu này"));

        if (request.getDocumentFile() != null){
            cloudinaryService.deleteFile(document.getFilePublicId(), "raw");
            CloudinaryResponse response = cloudinaryService.uploadDocument(request.getDocumentFile());
            document.setFileUrl(response.getUrl());
            document.setFilePublicId(response.getPublicId());

            String originalFileName = request.getDocumentFile().getOriginalFilename();
            String fileType = "UNKNOWN";
            if(originalFileName != null && originalFileName.contains(".")){
                fileType = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toUpperCase();
            }
            document.setFileType(FileType.valueOf(fileType));

            double mb = request.getDocumentFile().getSize() / (1024.0 * 1024.0);
            BigDecimal fileSizeMb = BigDecimal.valueOf(Math.round(mb * 100.0) / 100.0);
            document.setFileSizeMb(fileSizeMb);
        }

        if (request.getCoverFile() != null){
            if(document.getCoverUrl() != null){
                cloudinaryService.deleteFile(document.getCoverPublicId(), "image");
            }
            CloudinaryResponse response = cloudinaryService.uploadCover(request.getCoverFile());
            document.setCoverUrl(response.getUrl());
            document.setCoverPublicId(response.getPublicId());
        }

        if(request.getDocumentStatus() != null) document.setDocumentStatus(request.getDocumentStatus());
        if(request.getTitle() != null) document.setTitle(request.getTitle());
        if(request.getAuthor() != null) document.setAuthor(request.getAuthor());
        if(request.getPublisher() != null) document.setPublisher(request.getPublisher());
        if(request.getPublishedYear() != null) document.setPublishedYear(request.getPublishedYear());
        if(request.getDescription() != null) document.setDescription(request.getDescription());
        if(request.getPublicAccess() != null) document.setPublicAccess(request.getPublicAccess());

        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục này"));
            document.setCategory(category);
        }

        if(request.getTagNames() != null && !request.getTagNames().isEmpty()){
            document.setTags(tagService.findOrCreateTag(request.getTagNames()));
        }

        document = documentRepository.save(document);

        return DocumentResponse.fromEntity(document);
    }

}
