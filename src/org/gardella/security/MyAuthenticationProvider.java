package org.gardella.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gardella.security.dao.UserDAO;
import org.gardella.security.service.LoginService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;



public class MyAuthenticationProvider implements AuthenticationProvider{

    protected final Log logger = LogFactory.getLog(getClass());
    
    LoginService loginService;
    UserDAO userDAO;
    
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if(auth instanceof UsernamePasswordAuthenticationToken){
            String email = auth.getName();
            String pass = (String) auth.getCredentials();
            WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
            String sessionId = details.getSessionId();
            return loginService.login(email, pass, sessionId);
        }else if(auth instanceof PreAuthenticatedAuthenticationToken){
            String email = auth.getName();
            String sessionKey = (String) auth.getCredentials();
            return loginService.loginWithSessionKey(sessionKey, email);
        }
        return null;
    }

    /**
     * Supports two token types so far.
     * 
     */
    @Override
    public boolean supports(Class<? extends Object> arg0) {
        if(arg0 == UsernamePasswordAuthenticationToken.class){
            return true;
        }else if(arg0 == PreAuthenticatedAuthenticationToken.class) {
            return true;
        }
        return false;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
}
