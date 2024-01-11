package com.wanted.matitnyam.resolver;

import com.wanted.matitnyam.dto.Principal;
import com.wanted.matitnyam.dto.PrincipalDto;
import com.wanted.matitnyam.exception.UnauthorizedException;
import com.wanted.matitnyam.service.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class PrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthTokenService authTokenService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(PrincipalDto.class) &&
                parameter.hasParameterAnnotation(Principal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authorizationString = request.getHeader("Authorization");
        if (authorizationString == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        String[] splitAuthorizationString = authorizationString.split(" ");
        String token = splitAuthorizationString[1];
        return authTokenService.parseToken(token);
    }

}
