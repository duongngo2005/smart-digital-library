package com.ndd.digitallibrary.service;

import com.ndd.digitallibrary.dto.request.ChangePasswordRequest;
import com.ndd.digitallibrary.dto.request.UpdateProfileRequest;
import com.ndd.digitallibrary.dto.response.CloudinaryResponse;
import com.ndd.digitallibrary.dto.response.UserResponse;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.enums.SubscriptionTier;
import com.ndd.digitallibrary.exception.AppException;
import com.ndd.digitallibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder encoder;

    @Transactional
    public void checkAndSyncUserState(User user){
        if(user.getSubscriptionTier() != SubscriptionTier.MEMBER
                && user.getSubscriptionUntil() != null
                && user.getSubscriptionUntil().isBefore(LocalDateTime.now())){

            user.setSubscriptionTier(SubscriptionTier.MEMBER);
            user.setSubscriptionStartAt(null);
            user.setSubscriptionUntil(null);
            user.setCurrentCycleEnd(null);
            user.setDownloadedThisMonth(0);
            userRepository.save(user);
        }
        if(user.getCurrentCycleEnd() != null && LocalDateTime.now().isAfter(user.getCurrentCycleEnd())){
            user.setDownloadedThisMonth(0);
            user.setCurrentCycleEnd(user.getCurrentCycleEnd().plusMonths(1));
            userRepository.save(user);
        }
    }

    public UserResponse getMyUserProfile(User user){
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng này"));

        user.setFullName(request.getFullName());
        return UserResponse.fromEntity(userRepository.save(user));
    }

    public UserResponse updateAvatar(Long userId, MultipartFile avatar){

        if(avatar == null || avatar.isEmpty()){
            throw new AppException(HttpStatus.BAD_REQUEST, "File ảnh không đươc để trống");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng này"));

        if(user.getAvatarUrl() != null && user.getAvatarPublicId() != null){
            cloudinaryService.deleteFile(user.getAvatarPublicId(), "image");
        }

        CloudinaryResponse response = cloudinaryService.uploadAvatar(avatar);
        user.setAvatarUrl(response.getUrl());
        user.setAvatarPublicId(response.getPublicId());

        return UserResponse.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng này"));

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new AppException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không đúng");
        }
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new AppException(HttpStatus.BAD_REQUEST, "Mật khẩu mới và xác nhận mật khẩu không trùng khớp");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}
