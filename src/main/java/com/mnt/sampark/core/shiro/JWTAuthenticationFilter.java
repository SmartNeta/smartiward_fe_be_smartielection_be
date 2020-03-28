package com.mnt.sampark.core.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTAuthenticationFilter extends AuthenticatingFilter {

    protected static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {

        if (isLoggedAttempt(request, response)) {
            String jwtToken = getAuthzHeader(request);
            if (jwtToken != null) {
                return createToken(jwtToken);
            }
        }

        return new UsernamePasswordToken();
    }

    public JWTAuthenticationToken createToken(String token) {
        Algorithm algorithmHS = null;
        try {
            algorithmHS = Algorithm.HMAC256("secrEt123seCret");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JWTVerifier verifier = JWT.require(algorithmHS)
                .withIssuer("auth0")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return new JWTAuthenticationToken(jwt.getSubject(), token);

    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (request instanceof HttpServletRequest) {
            if (((HttpServletRequest) request).getMethod().toUpperCase().equals("OPTIONS")) {
                return true;
            }
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false;
        if (isLoginRequest(request, response) || isLoggedAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
        }

        if (!loggedIn) {
            HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return loggedIn;
    }

    protected boolean isLoggedAttempt(ServletRequest request, ServletResponse response) {
        String authzHeader = getAuthzHeader(request);
        return authzHeader != null;
    }

    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(AUTHORIZATION_HEADER);
    }

}
