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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, User user){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đánh giá này"));

        if(!user.getId().equals(review.getUser().getId())){
            throw new RuntimeException("Bạn không có quyền sửa đánh giá này");
        }

        Document document = review.getDocument();
        document.setAverageRating(calcAverageRatingUpdate(review.getRating(), request.getRating(), document));
        documentRepository.save(document);

        review.setComment(request.getComment());
        review.setRating(request.getRating());

        review = reviewRepository.save(review);
        return ReviewResponse.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAllReviews(Long documentId, Pageable pageable){

        if(!documentRepository.existsById(documentId)){
            throw new RuntimeException("Tài liệu không tồn tại");
        }

        return reviewRepository.findByDocumentId(documentId, pageable).map(ReviewResponse::fromEntity);
    }

    private BigDecimal calcAverageRatingUpdate(short oldRating, short newRating, Document document){
        long count = document.getReviewCount();
        BigDecimal oldAvg = document.getAverageRating();

        BigDecimal totalOldScore = oldAvg.multiply(BigDecimal.valueOf(count));

        BigDecimal totalNewScore = totalOldScore.add(BigDecimal.valueOf(newRating)).subtract(BigDecimal.valueOf(oldRating));

        return totalNewScore.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
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
