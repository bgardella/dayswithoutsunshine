package org.gardella.security;

import java.io.Serializable;

import org.gardella.security.model.User;
import org.gardella.security.model.UserStatus;
import org.gardella.security.model.UserType;


/**
 * Small version of our User model object that will live in the Spring Security Context,
 * also known as "the session".
 * 
 * @author Ben
 *
 */
public class MyUserAuthentication implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String username;
    private long userId = 0;
    private String email;     
    private UserType type;    
    private UserStatus status;
    
    private String countryCode;
    private String remoteAddress;
    private String userAgent;
    
    private String sessionKey;
    
    //support anonymous authentications
    public MyUserAuthentication(){
        type = UserType.VISITOR;
    }
    
    public MyUserAuthentication( User user ){
        this.userId = user.getId();
        this.email = user.getEmail();
        this.type = user.getUserType();
        this.status = user.getUserStatus();
    }

    public String getUsername() {
        return username;
    }
    
    public long getUserId() {
        return this.userId;
    }

    public String getEmail() {
        return email;
    }

    public UserType getType() {
        return type;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
