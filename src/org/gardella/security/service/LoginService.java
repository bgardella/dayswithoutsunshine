package org.gardella.security.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gardella.common.jdbc.ZeroRowsAffectedException;
import org.gardella.security.LoginCode;
import org.gardella.security.LoginState;
import org.gardella.security.MyUserAuthentication;
import org.gardella.security.dao.UserDAO;
import org.gardella.security.model.LoginSession;
import org.gardella.security.model.User;
import org.gardella.security.model.UserStatus;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.base.Preconditions;


public class LoginService{

    protected final Log logger = LogFactory.getLog(getClass());
    
    private UserDAO userDAO;  

    public LoginState checkLogin( String email, String password ){
        
        User user = userDAO.getUser(email);
        LoginState state = new LoginState( user );
        
        if( user == null ){
            state.setCode(LoginCode.INVALID_EMAIL);
        }
        else if( !user.getPassword().equals( DigestUtils.md5Hex(password) ) ){
            state.setCode(LoginCode.INVALID_PASSWORD);
        }
        else if (user.getUserStatus()!=UserStatus.ACTIVE) {
            
            if(user.getUserStatus() == UserStatus.PENDING){
                state.setCode(LoginCode.PENDING_USER);
            }else{
                state.setCode(LoginCode.INACTIVE_USER);
            }
        }else {
            state.setCode(LoginCode.SUCCESS);
        }
        
        return state;
    }
    
    
    public LoginState checkSessionKey( String sessionKey ){
        long userId = getUserIdFromValidSession( sessionKey );
        LoginState state;
        
        if(userId > 0 ){
            User user = userDAO.getUser(userId);
            state = new LoginState(user);
            state.setCode(LoginCode.HEADER_AUTH);
        }else{
            state = new LoginState(null);
            state.setCode(LoginCode.PERMISSION_DENIED);
        }
        
        return state;
    }
    

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    private Authentication success(LoginState state, String sessionId) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        
        authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        
        if(state.getCode() == LoginCode.PENDING_USER){
            authorities.add(new GrantedAuthorityImpl("ROLE_NEEDS_EMAIL_AUTH"));
        }   
        if(state.getUser().admin()){
            authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        }
        
        //save a simple bean that is a subset of our User model object
        MyUserAuthentication user = new MyUserAuthentication(state.getUser()); 
        user.setCountryCode(LocaleContextHolder.getLocale().getCountry());
  
        // Longest. Class name.  EVAR
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, state.getUser().getPassword(), authorities);
        
        if(state.getCode() != LoginCode.HEADER_AUTH){
            //save it in the DB (user_session)
            saveSessionId( sessionId, user.getUserId() );
        }
        SecurityContextHolder.getContext().setAuthentication(token);
        return token;            
    }
    
    public Authentication login(String user, String pass, String sessionKey) {
        Preconditions.checkNotNull(user);
        Preconditions.checkNotNull(pass);        
        Preconditions.checkNotNull(sessionKey);
        LoginState state = this.checkLogin(user, pass);
        return login2(state, sessionKey);
    }

    public Authentication loginWithSessionKey(String sessionKey, String email) {    
        Preconditions.checkNotNull(sessionKey);
        LoginState state = this.checkSessionKey(sessionKey);
        if(state.getUser() != null && state.getUser().getEmail().equalsIgnoreCase(email)){
            
            return login2(state, sessionKey);
        }
         
        throw new BadCredentialsException("email does not match sessionkey");        
    }    
    
    private Authentication login2(LoginState state, String session) {
        LoginCode code = state.getCode();
        switch (code) {
            case SUCCESS:
            case HEADER_AUTH:
            case PENDING_USER:
                return success(state, session);
            case INACTIVE_USER:
                throw new CredentialsExpiredException(code.toString());
            default:
                throw new BadCredentialsException(code.toString());
        }
    }
    
    private void saveSessionId( String sessionId, long userId ){
        
        if(getUserIdFromValidSession(sessionId) != userId){ //if the session is still valid, leave it alone
        
            try{
                userDAO.deleteLoginSession(sessionId);    // clear out any old sessions w/ this id
                
            }catch(ZeroRowsAffectedException e){ //catch exception if the sessionId is already gone
                //noop
            }
            LoginSession loginSession = new LoginSession();
            loginSession.setSessionKey(sessionId);
            loginSession.setUserId(userId);
            long now = System.currentTimeMillis();
            loginSession.setCreatedOn(new Date(now));
            loginSession.setModifiedOn(new Date(now));
            long endTime = now + (DateUtils.MILLIS_PER_HOUR * 2);  //session expires in 2 hours
            loginSession.setExpiredOn(new Date(endTime));
            userDAO.createLoginSession(loginSession); 
        }
    }

    private long getUserIdFromValidSession( String sessionId ){
        LoginSession sess = userDAO.getLoginSession(sessionId);
        if(sess != null && sess.getExpiredOn().after(new Date())){
            return sess.getUserId();
        }
        
        //logger.error("session id [" + sessionId + "] is invalid or null in the db.");
        if(sess == null){
            logger.error("session row is null for id: [" + sessionId + "]");
        }else{
            logger.error("session row invalid for id: [" + sessionId + "] expire stamp: [" + sess.getExpiredOn().toString() + "]");
        }
        
        return -1L;
    }
    
}
