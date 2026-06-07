package com.ndd.digitallibrary.service;

import com.ndd.digitallibrary.dto.response.AccessLogResponse;
import com.ndd.digitallibrary.dto.response.DocumentSummaryResponse;
import com.ndd.digitallibrary.entity.AccessLog;
import com.ndd.digitallibrary.entity.Document;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.repository.AccessLogRepository;
import com.ndd.digitallibrary.repository.DocumentRepository;
import com.ndd.digitallibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public boolean hasUserRead(Long documentId, Long userId){
        return accessLogRepository.existsByUserIdAndDocumentId(userId, documentId);
    }

    @Transactional
    public void recordReading(Long documentId, Long userId) {

        accessLogRepository.findByUserIdAndDocumentId(userId, documentId)
                .ifPresentOrElse(
                        accessLog -> {
                            accessLog.setLastReadAt(LocalDateTime.now());
                        },
                        () -> {
                            Document document = documentRepository.findById(documentId)
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu này"));

                            User user = userRepository.findById(userId)
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng này"));

                            AccessLog newAccessLog = AccessLog.builder()
                                    .user(user)
                                    .document(document)
                                    .lastReadPage(1)
                                    .lastReadAt(LocalDateTime.now())
                                    .hasDownloaded(false)
                                    .build();

                            newAccessLog = accessLogRepository.save(newAccessLog);
                            document.setViewCount(document.getViewCount() + 1);
                        }

                );
    }

    @Transactional
    public void recordDownload(Long documentId, Long userId){

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu này"));

        accessLogRepository.findByUserIdAndDocumentId(userId, documentId)
                .ifPresentOrElse(
                        accessLog -> {
                            if(!accessLog.isHasDownloaded()){
                                accessLog.setHasDownloaded(true);
                                document.setDownloadCount(document.getDownloadCount() + 1);
                            }
                            accessLog.setLastReadAt(LocalDateTime.now());
                        },
                        () -> {
                            User user = userRepository.findById(userId)
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));

                            AccessLog newAccessLog = AccessLog.builder()
                                    .user(user)
                                    .lastReadAt(LocalDateTime.now())
                                    .lastReadPage(1)
                                    .document(document)
                                    .hasDownloaded(true)
                                    .build();

                            accessLogRepository.save(newAccessLog);

                            document.setViewCount(document.getViewCount() + 1);
                            document.setDownloadCount(document.getDownloadCount() + 1);
                        }
                );
    }

    public Page<AccessLogResponse> getUserReadingHistory(Long userId, Pageable pageable){

        return accessLogRepository.findByUserIdOrderByLastReadAtDesc(userId, pageable).map(log -> {
            DocumentSummaryResponse response = DocumentSummaryResponse.fromEntity(log.getDocument());

            return AccessLogResponse.builder()
                    .documentSummaryResponse(response)
                    .lastReadAt(log.getLastReadAt())
                    .lastReadPage(log.getLastReadPage())
                    .hasDownloaded(log.isHasDownloaded())
                    .build();
        });
    }

    @Transactional
    public void updateReadingProgress(Long userId, Long documentId, int page){

        AccessLog accessLog = accessLogRepository.findByUserIdAndDocumentId(userId, documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch sử truy cập"));

        accessLog.setLastReadPage(page);
    }

}
