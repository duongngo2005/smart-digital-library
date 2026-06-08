package com.ndd.digitallibrary.service;

import com.ndd.digitallibrary.dto.request.CreateDocumentRequest;
import com.ndd.digitallibrary.dto.request.UpdateDocumentRequest;
import com.ndd.digitallibrary.dto.response.CloudinaryResponse;
import com.ndd.digitallibrary.dto.response.DocumentResponse;
import com.ndd.digitallibrary.dto.response.DocumentSummaryResponse;
import com.ndd.digitallibrary.entity.*;
import com.ndd.digitallibrary.enums.FileType;
import com.ndd.digitallibrary.enums.Role;
import com.ndd.digitallibrary.enums.SubscriptionTier;
import com.ndd.digitallibrary.repository.AccessLogRepository;
import com.ndd.digitallibrary.repository.CategoryRepository;
import com.ndd.digitallibrary.repository.DocumentRepository;
import com.ndd.digitallibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final AccessLogService accessLogService;
    private final AccessLogRepository accessLogRepository;
    private final UserService userService;

    @Value("${app.subscription.plus-download-limit:100}")
    private int plusDownloadLimit;

    @Value("${app.subscription.pro-download-limit:300}")
    private int proDownloadLimit;

    public DocumentResponse getDocumentById(Long id){

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu này"));

        return DocumentResponse.fromEntity(document);
    }

    public List<DocumentSummaryResponse> getAllDocuments(){
        return documentRepository.findAll().stream().map(DocumentSummaryResponse::fromEntity).toList();
    }

    @Transactional
    public DocumentResponse createDocument(CreateDocumentRequest request, String userEmail){

        if(documentRepository.existsByTitleAndAuthorAndPublisherAndPublishedYear(
                request.getTitle(),
                request.getAuthor(),
                request.getPublisher(),
                request.getPublishedYear()
        )){
            throw new RuntimeException("Tài liệu đã tồn tại trong hệ thống");
        }

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

    public String getStreamUrl(Long documentId, Long userId){

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấ tài liệu này"));

        if(!document.isPublicAccess() && userId == null){
            throw new RuntimeException("Đăng nhập để đọc tài liệu này");
        }

        if(userId != null){
            accessLogService.recordReading(documentId, userId);
        }

        return cloudinaryService.generateSignedUrl(document.getFilePublicId(), "raw");
    }

    @Transactional
    public String getDownloadUrl(Long documentId, Long userId){
        if(userId == null){
            throw new RuntimeException("Đăng nhập để tải tài liệu");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

        if(user.getRole() == Role.ADMIN || user.getRole() == Role.LIBRARIAN){
            accessLogService.recordDownload(documentId, userId);
            return cloudinaryService.generateDownloadUrl(document.getFilePublicId(), "raw");
        }

        userService.checkAndSyncUserState(user);

        if(user.getSubscriptionTier() == SubscriptionTier.MEMBER){
            throw new RuntimeException("Vui lòng nâng cấp gói PLUS hoặc PRO để tải xuống tài liệu");
        }

        boolean isFirstTimeDownload = true;
        Optional<AccessLog> accessLog = accessLogRepository.findByUserIdAndDocumentId(userId, documentId);
        if(accessLog.isPresent() && accessLog.get().isHasDownloaded()){
            isFirstTimeDownload = false;
        }

        if(isFirstTimeDownload){

            int limit = (user.getSubscriptionTier() == SubscriptionTier.PLUS) ? plusDownloadLimit : proDownloadLimit;

            if(user.getDownloadedThisMonth() >= limit){
                throw new RuntimeException("Bạn đã dùng hết lượt tải tài liệu trong tháng");
            }

            user.setDownloadedThisMonth(user.getDownloadedThisMonth() + 1);
            userRepository.save(user);
        }

        accessLogService.recordDownload(documentId, userId);
        return cloudinaryService.generateDownloadUrl(document.getFilePublicId(), "raw");
    }
}
