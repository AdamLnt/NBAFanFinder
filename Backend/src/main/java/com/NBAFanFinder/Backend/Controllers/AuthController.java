package com.NBAFanFinder.Backend.Controllers;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.NBAFanFinder.Backend.DTOs.AuthResponse;
import com.NBAFanFinder.Backend.DTOs.LoginRequest;
import com.NBAFanFinder.Backend.DTOs.RegisterRequest;
import com.NBAFanFinder.Backend.Security.AuthCookieService;
import com.NBAFanFinder.Backend.Services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService cookieService;

    public AuthController(AuthService authService, AuthCookieService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse body = authService.login(request);
        if (body.token() != null) {
            cookieService.setAuthCookie(response, body.token());
        }
        // Le token reste dans le body pour les clients non-navigateur (CLI, mobile).
        // Le frontend web ne doit PAS le stocker (cookie HttpOnly gere la session).
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieService.clearAuthCookie(response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/activate/{token}")
    public ResponseEntity<Void> activate(@PathVariable String token) {
        authService.activate(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Auth service is running!");
    }
}
