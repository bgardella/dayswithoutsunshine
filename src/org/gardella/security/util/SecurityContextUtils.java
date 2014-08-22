package org.gardella.security.util;

import java.util.Collection;

import org.gardella.security.MyUserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.context.request.RequestContextHolder;


public class SecurityContextUtils {

    public static MyUserAuthentication currentUserDetails(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            
            for(GrantedAuthority auth : authentication.getAuthorities()){
                if(auth.getAuthority().equals("ROLE_ANONYMOUS")){
                    return new MyUserAuthentication(); //return an anonymous user
                }
            }
            
            MyUserAuthentication auth = (MyUserAuthentication)authentication.getPrincipal();
            String remoteAddress = null;

            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            if (details != null) {
                remoteAddress = details.getRemoteAddress();
            } else {
                // if no details exist, just grab from the request.
                remoteAddress = RequestContextHolder.currentRequestAttributes().getSessionId();
            }
            auth.setRemoteAddress( remoteAddress ); //useful

            return auth;
        }
        return null;
    }
 
    public static String currentSessionId(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();        
        if (authentication != null) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            if (details != null) {
                return details.getSessionId();
            }
        }
        return null;
    }
    
    public static boolean isAdmin(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();        
        if (authentication != null) {
            Collection<GrantedAuthority> grants = authentication.getAuthorities();
            for(GrantedAuthority g : grants){
                if(g.getAuthority().equals("ROLE_ADMIN")){
                    return true;
                }
            }
        }
        return false;
    }
}
