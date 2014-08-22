package org.gardella.security.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Required user bean for spring's default hash-based cookie approach.
 * 
 * @author Ben
 *
 */
public class RememberMeUserDetails implements UserDetails {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String password;
    private String email;
    private String sessionKey;
    
    public RememberMeUserDetails(){
        //noop
    }
    
    public RememberMeUserDetails( String email, String password ){
        this.email = email;
        this.password = password;
    }
    
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
    
    public String getSessionKey(){
        return this.sessionKey;
    }
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }
}
