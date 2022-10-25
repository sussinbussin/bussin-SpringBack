package com.bussin.SpringBack.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication
        .preauth.AbstractPreAuthenticatedProcessingFilter;

public class TokenAuthFilter
        extends AbstractPreAuthenticatedProcessingFilter {

    private final String authHeaderName = "Authorization";

    /**
     * Gets principal from request. Ignored.
     * @param request The request to get principal from
     * @return The authenticated principal
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return "N/A";
    }

    /**
     * Gets credentials from request
     * @param request Request to get credentials from
     * @return The credentials
     */
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(authHeaderName);
    }
}