package com.ndd.digitallibrary.service;

import com.ndd.digitallibrary.dto.request.UpdateUserStatusRequest;
import com.ndd.digitallibrary.dto.request.UserFilterRequest;
import com.ndd.digitallibrary.dto.response.UserResponse;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.repository.UserRepository;
import com.ndd.digitallibrary.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUser(UserFilterRequest filterRequest, Pageable pageable){

        Specification<User> specification = Specification.allOf(
                UserSpecification.hasKeyword(filterRequest.getKeyword()),
                UserSpecification.hasRole(filterRequest.getRole()),
                UserSpecification.hasUserStatus(filterRequest.getUserStatus()),
                UserSpecification.hasSubscription(filterRequest.getSubscriptionTier())
        );

        return  userRepository.findAll(specification, pageable).map(UserResponse::fromEntity);
    }

    @Transactional
    public UserResponse updateUserStatus(UpdateUserStatusRequest request, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng này"));

        user.setUserStatus(request.getUserStatus());
        user = userRepository.save(user);
        return UserResponse.fromEntity(user);
    }
}
