package org.gardella.security.service;

import org.gardella.security.dao.UserDAO;
import org.gardella.security.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Required service implementation for spring's default hash-based cookie approach.
 * 
 * @author Ben
 *
 */
public class RememberMeUserDetailsService implements UserDetailsService {

    private UserDAO userDAO;
    
    
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
       
        User user = userDAO.getUser(username);
        UserDetails details;
        if(user != null){
            details = new RememberMeUserDetails(user.getEmail(), user.getPassword());
        }else{
            details = new RememberMeUserDetails();  //invalid details needed by framework to remove remember-me cookie if session has expired;
        }
        
        return details;
    }
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
