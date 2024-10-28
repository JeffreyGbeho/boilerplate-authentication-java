package com.selfengineerjourney.auth.security.oauth;

import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.exception.UserNotFoundException;
import com.selfengineerjourney.auth.repository.UserRepository;
import com.selfengineerjourney.auth.security.jwt.JwtService;
import com.selfengineerjourney.auth.utils.oauth.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final UserRepository userRepository;

    @Value("${app.client_url}")
    private String clientUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElseThrow();

        Object principal = authentication.getPrincipal();
        User user = null;
        if (principal instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal;
            user = userRepository.findByEmailOrUsername(oidcUser.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
        }

        String accessToken = "";
        String refreshToken = "";
        if (user != null) {
            accessToken = jwtService.generateAccessToken(user);
            refreshToken = jwtService.generateRefreshToken(user);

            userRepository.save(user);
        } else {
            throw new RuntimeException("A problem has occured while trying to access the user");
        }

        return UriComponentsBuilder.fromUriString(targetUrl).queryParam("access_token", accessToken).queryParam("refresh_token", refreshToken).build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        String[] urls = clientUrl.split(",");
        List<String> authorizedRedirectUris = new ArrayList<>();
        for (String url : urls) {
            authorizedRedirectUris.add(url.trim() + "/api/auth/oauth");
        }

        return authorizedRedirectUris.stream().anyMatch(authorizedRedirectUri -> {
            URI authorizedURI = URI.create(authorizedRedirectUri);
            return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort();
        });
    }
}

