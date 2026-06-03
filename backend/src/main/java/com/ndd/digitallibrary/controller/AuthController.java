package com.ndd.digitallibrary.controller;

import com.ndd.digitallibrary.dto.request.LoginRequest;
import com.ndd.digitallibrary.dto.request.RegisterRequest;
import com.ndd.digitallibrary.dto.response.AuthResponse;
import com.ndd.digitallibrary.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ){

        AuthResponse authResponse = authService.register(request);

        if (authResponse.getRefreshToken() != null){
            addRefreshTokenCookie(response, authResponse.getRefreshToken());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){

        AuthResponse authResponse = authService.login(request);

        addRefreshTokenCookie(response, authResponse.getRefreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ){

        if(refreshToken == null || refreshToken.isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthResponse response = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ){

        if (refreshToken != null){
            authService.logout(refreshToken);
        }

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }
}
