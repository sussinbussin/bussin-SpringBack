package com.bussin.SpringBack.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication
        .preauth.AbstractPreAuthenticatedProcessingFilter;

public class TokenAuthFilter
        extends AbstractPreAuthenticatedProcessingFilter {

    private String authHeaderName = "Authorization";

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(authHeaderName);
    }
}