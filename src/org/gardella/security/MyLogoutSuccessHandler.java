package org.gardella.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.gardella.security.dao.UserDAO;
import org.gardella.security.model.LoginSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


public class MyLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

    UserDAO userDAO;
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //kill the session 
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        if(authentication != null){
            
            //delete the session from the DB
            if(authentication.getPrincipal() != null && authentication.getPrincipal() instanceof MyUserAuthentication){
                MyUserAuthentication auth = (MyUserAuthentication)authentication.getPrincipal();
                
                LoginSession sess = userDAO.getLoginSession(auth.getSessionKey());
                if(sess != null && sess.getUserId() == auth.getUserId()){
                    userDAO.deleteLoginSession(auth.getSessionKey());
                }
            }
            
            //for good measure
            authentication.setAuthenticated(false);
            
        }
        
        super.handle(request, response, authentication);
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }    
    
}
