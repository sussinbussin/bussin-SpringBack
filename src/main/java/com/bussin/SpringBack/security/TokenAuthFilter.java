package com.bussin.SpringBack.security;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class TokenAuthFilter
        extends AbstractPreAuthenticatedProcessingFilter {

    private static final String AUTH_HEADER_NAME = "Authorization";

    /**
     * Gets principal from request. Ignored.
     *
     * @param request The request to get principal from
     * @return The authenticated principal
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
        return "N/A";
    }

    /**
     * Gets credentials from request
     *
     * @param request Request to get credentials from
     * @return The credentials
     */
    @Override
    protected Object getPreAuthenticatedCredentials(final HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER_NAME);
    }
}