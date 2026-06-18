package com.NBAFanFinder.Backend.unit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import com.NBAFanFinder.Backend.Security.AuthCookieService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - AuthCookieService")
public class AuthCookieServiceTest {

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    private AuthCookieService cookieService;

    @BeforeEach
    void setUp() {
        cookieService = new AuthCookieService();
        ReflectionTestUtils.setField(cookieService, "secure", true);
        ReflectionTestUtils.setField(cookieService, "sameSite", "Strict");
        ReflectionTestUtils.setField(cookieService, "expirationMs", 3600000L);
    }

    @Test
    @DisplayName("setAuthCookie écrit un cookie jwt HttpOnly avec le token")
    void shouldSetAuthCookie() {
        cookieService.setAuthCookie(response, "my-jwt-token");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), captor.capture());
        String cookie = captor.getValue();
        assertThat(cookie).contains("jwt=my-jwt-token");
        assertThat(cookie).contains("HttpOnly");
        assertThat(cookie).contains("Secure");
        assertThat(cookie).contains("SameSite=Strict");
        assertThat(cookie).contains("Path=/");
    }

    @Test
    @DisplayName("clearAuthCookie écrit un cookie jwt expiré (Max-Age=0)")
    void shouldClearAuthCookie() {
        cookieService.clearAuthCookie(response);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), captor.capture());
        String cookie = captor.getValue();
        assertThat(cookie).contains("jwt=");
        assertThat(cookie).contains("Max-Age=0");
    }

    @Test
    @DisplayName("readAuthCookie retourne la valeur du cookie jwt présent")
    void shouldReadAuthCookie() {
        Cookie[] cookies = { new Cookie("autre", "x"), new Cookie("jwt", "stored-token") };
        when(request.getCookies()).thenReturn(cookies);

        assertThat(cookieService.readAuthCookie(request)).isEqualTo("stored-token");
    }

    @Test
    @DisplayName("readAuthCookie retourne null quand aucun cookie n'est présent")
    void shouldReturnNullWhenNoCookies() {
        when(request.getCookies()).thenReturn(null);

        assertThat(cookieService.readAuthCookie(request)).isNull();
    }

    @Test
    @DisplayName("readAuthCookie retourne null quand le cookie jwt est absent")
    void shouldReturnNullWhenJwtCookieMissing() {
        Cookie[] cookies = { new Cookie("session", "abc") };
        when(request.getCookies()).thenReturn(cookies);

        assertThat(cookieService.readAuthCookie(request)).isNull();
    }
}
