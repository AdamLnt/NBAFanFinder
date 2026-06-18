package com.NBAFanFinder.Backend.unit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.NBAFanFinder.Backend.Entities.User;
import com.NBAFanFinder.Backend.Repositories.UserRepository;
import com.NBAFanFinder.Backend.Security.AuthCookieService;
import com.NBAFanFinder.Backend.Security.JwtFilter;
import com.NBAFanFinder.Backend.Security.JwtUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - JwtFilter")
public class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthCookieService cookieService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Dupont", "Jean", "jean@email.com", "pwd");
        user.setId(1L);
        user.setActif(true);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Authentifie l'utilisateur quand le token du cookie est valide et le compte actif")
    void shouldAuthenticateFromValidCookieToken() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn("valid-token");
        when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("jean@email.com");
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(user));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo("jean@email.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Lit le token depuis le header Authorization quand le cookie est absent")
    void shouldAuthenticateFromBearerHeader() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer header-token");
        when(jwtUtil.getUsernameFromToken("header-token")).thenReturn("jean@email.com");
        when(jwtUtil.validateToken("header-token")).thenReturn(true);
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(user));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Ne définit aucune authentification quand aucun token n'est présent")
    void shouldSkipWhenNoToken() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Ignore un header Authorization sans préfixe Bearer")
    void shouldIgnoreNonBearerHeader() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("N'authentifie pas quand le token est invalide")
    void shouldNotAuthenticateWhenTokenInvalid() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn("bad-token");
        when(jwtUtil.getUsernameFromToken("bad-token")).thenReturn("jean@email.com");
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("N'authentifie pas quand l'extraction du username échoue")
    void shouldHandleTokenParsingException() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn("corrupt-token");
        when(jwtUtil.getUsernameFromToken("corrupt-token")).thenThrow(new RuntimeException("boom"));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("N'authentifie pas quand l'utilisateur n'existe pas")
    void shouldNotAuthenticateWhenUserNotFound() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn("valid-token");
        when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("inconnu@email.com");
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(userRepository.findByEmail("inconnu@email.com")).thenReturn(Optional.empty());

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("N'authentifie pas un compte inactif")
    void shouldNotAuthenticateInactiveUser() throws Exception {
        user.setActif(false);
        when(cookieService.readAuthCookie(request)).thenReturn("valid-token");
        when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("jean@email.com");
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(userRepository.findByEmail("jean@email.com")).thenReturn(Optional.of(user));

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Transmet toujours la requête le long de la chaîne de filtres")
    void shouldAlwaysContinueChain() throws Exception {
        when(cookieService.readAuthCookie(request)).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());
    }
}
