package org.gardella.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;


public class MySavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {

        String targetUrl = null;

        SecurityContext securityContext = SecurityContextHolder.getContext();

        Collection<GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();

        if (authorities.contains(new GrantedAuthorityImpl("ROLE_NEEDS_EMAIL_AUTH"))) {
            targetUrl = "/authenticate";
        } else if (authorities.contains(new GrantedAuthorityImpl("ROLE_USER"))) {
            targetUrl = this.getDefaultTargetUrl();
        } else {
            targetUrl = super.determineTargetUrl(request, response);
        }

        return targetUrl;
    }
    
    
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

        //check for explicit redirect first
        if( StringUtils.hasText(request.getParameter("x-redirect")) ){
        
            String redir = request.getParameter("x-redirect");
            logger.debug("Redirecting to explicit redirect Url: " + redir);
            getRedirectStrategy().sendRedirect(request, response, redir);

            return;
        }
        
        super.onAuthenticationSuccess(request, response, authentication);
            
    }
    
}
