package com.ex.palago.security.jwt;

import com.ex.palago.auth.model.request.SignInRequest;
import com.ex.palago.auth.model.response.SignInRequestValidationResult;
import com.ex.palago.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
            String principal = (String) authenticationToken.getPrincipal();
            String credential = (String) authenticationToken.getCredentials();

            log.info("principal : {}", principal);
            log.info("credential : {}", credential);

            SignInRequestValidationResult signInRequestValidationResult = authService.validateSignInRequest(new SignInRequest(principal, credential));

//            UserDetails userDetails =
//                    new AuthDetails(accessTokenInfo.getUserId().toString(), accessTokenInfo.getRole());
//            return new UsernamePasswordAuthenticationToken(
//                    userDetails, "user", userDetails.getAuthorities());

            return new UsernamePasswordAuthenticationToken(principal, null,
                    AuthorityUtils.createAuthorityList(signInRequestValidationResult.getRoleKey())
            );
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ClassUtils.isAssignable(UsernamePasswordAuthenticationToken.class, authentication);
    }
}
