package com.ndd.digitallibrary.service;

import com.ndd.digitallibrary.dto.request.ReviewRequest;
import com.ndd.digitallibrary.dto.response.ReviewResponse;
import com.ndd.digitallibrary.entity.Document;
import com.ndd.digitallibrary.entity.Review;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.repository.AccessLogRepository;
import com.ndd.digitallibrary.repository.DocumentRepository;
import com.ndd.digitallibrary.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AccessLogRepository accessLogRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public ReviewResponse createReview(Long documentId, User user, ReviewRequest request){
        if(!accessLogRepository.existsByUserIdAndDocumentId(user.getId(), documentId)){
            throw new RuntimeException("Bạn cần đọc tài liệu này trước khi viết đánh giá");
        }

        if(reviewRepository.existsByUserIdAndDocumentId(user.getId(), documentId)){
            throw new RuntimeException("Bạn đã viết đánh giá tài liệu này một lần rồi");
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu này"));

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .document(document)
                .user(user)
                .build();

        document.setAverageRating(calcAverageRating(request.getRating(), document));
        document.setReviewCount(document.getReviewCount() + 1);
        documentRepository.save(document);

        review = reviewRepository.save(review);

        return ReviewResponse.fromEntity(review);
    }

    private BigDecimal calcAverageRating(short newRating, Document document){
        long oldCount = document.getReviewCount();
        BigDecimal oldAvg = document.getAverageRating();
        long newCount = oldCount + 1;

        BigDecimal totalOldScore = oldAvg.multiply(BigDecimal.valueOf(oldCount));

        BigDecimal totalNewScore = totalOldScore.add(BigDecimal.valueOf(newRating));

        return totalNewScore.divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP);
    }

}
