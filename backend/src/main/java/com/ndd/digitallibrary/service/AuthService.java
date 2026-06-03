package com.ndd.digitallibrary.service;

import com.ndd.digitallibrary.config.JwtConfig;
import com.ndd.digitallibrary.dto.request.LoginRequest;
import com.ndd.digitallibrary.dto.request.RegisterRequest;
import com.ndd.digitallibrary.dto.response.AuthResponse;
import com.ndd.digitallibrary.dto.response.UserResponse;
import com.ndd.digitallibrary.entity.RefreshToken;
import com.ndd.digitallibrary.entity.User;
import com.ndd.digitallibrary.enums.Role;
import com.ndd.digitallibrary.enums.SubscriptionTier;
import com.ndd.digitallibrary.enums.UserStatus;
import com.ndd.digitallibrary.repository.RefreshTokenRepository;
import com.ndd.digitallibrary.repository.UserRepository;
import com.ndd.digitallibrary.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request){

        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email đã được sử dụng");
        }

        Role role = Role.USER;
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("LIBRARIAN")){
            role = Role.LIBRARIAN;
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .userStatus(role == Role.LIBRARIAN ? UserStatus.PENDING : UserStatus.ACTIVE)
                .subscriptionTier(SubscriptionTier.MEMBER)
                .build();

        user  = userRepository.save(user);

        if (user.getUserStatus() == UserStatus.PENDING){
            return AuthResponse.builder()
                    .accessToken(null)
                    .tokenType("Bearer")
                    .userResponse(UserResponse.fromEntity(user))
                    .build();
        }

        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        if (user.getUserStatus() == UserStatus.PENDING){
            throw new RuntimeException("Tài khoản đang chờ phê duyệt");
        }
        if (user.getUserStatus() == UserStatus.SUSPENDED){
            throw new RuntimeException("Tài khoản đang bị khóa");
        }

        checkAndUpdateSubscription(user);

        return createAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr){

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

        if (refreshToken.isRevoked()){
            throw new RuntimeException("Refresh token đã bị thu hồi");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        User user = refreshToken.getUser();

        checkAndUpdateSubscription(user);

        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .userResponse(UserResponse.fromEntity(user))
                .build();
    }

    @Transactional
    public void logout(String refreshTokenStr){
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    private void checkAndUpdateSubscription(User user){
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
    }

    private AuthResponse createAuthResponse(User user){

        String accessToken = jwtService.generateAccessToken(user);

        String refreshTokenStr = jwtService.generateRefreshToken(user);

        long refreshExpirationMs = jwtConfig.getRefreshExpirationMs();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiresAt(LocalDateTime.now().plusNanos(refreshExpirationMs * 1000000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(refreshTokenStr)
                .userResponse(UserResponse.fromEntity(user))
                .build();
    }
}
