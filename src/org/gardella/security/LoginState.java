package org.gardella.security;

import org.gardella.security.model.User;

public class LoginState {
    
    private User user;
    private LoginCode code;

    public LoginState( User user ){
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public LoginCode getCode() {
        return code;
    }
    public void setCode(LoginCode code) {
        this.code = code;
    }
    
}

