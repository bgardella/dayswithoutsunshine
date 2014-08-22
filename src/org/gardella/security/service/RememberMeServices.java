package org.gardella.security.service;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gardella.security.MyUserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;


/**
 * Needed to control how the remember-me credentials were extracted
 * to make a cookie-hash.
 * 
 * Also needed to explicitly set a "sessionKey" from the Cookie Token Signature.
 * 
 * @see TokenBasedRememberMeServices.makeTokenSignature()
 * 
 * @author Ben
 *
 */
public class RememberMeServices extends TokenBasedRememberMeServices {

    
    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
            HttpServletResponse response) {

        if (cookieTokens.length != 3) {
            throw new InvalidCookieException("Cookie token did not contain 3" +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        long tokenExpiryTime;

        try {
            tokenExpiryTime = new Long(cookieTokens[1]).longValue();
        }
        catch (NumberFormatException nfe) {
            throw new InvalidCookieException("Cookie token[1] did not contain a valid number (contained '" +
                    cookieTokens[1] + "')");
        }

        if (isTokenExpired(tokenExpiryTime)) {
            throw new InvalidCookieException("Cookie token[1] has expired (expired on '"
                    + new Date(tokenExpiryTime) + "'; current time is '" + new Date() + "')");
        }

        // Check the user exists.
        RememberMeUserDetails userDetails = (RememberMeUserDetails)getUserDetailsService().loadUserByUsername(cookieTokens[0]); 
        
        //Now check the Cookie Token Signature
        String cookieTokenSig = cookieTokens[2];
        
        userDetails.setSessionKey(cookieTokenSig); //re-using the sig as a session key here because more than likely the JSESSIONID has expired

        //now calculate a new sig and compare
        String expectedTokenSignature = makeTokenSignature(tokenExpiryTime, userDetails.getUsername(),
                userDetails.getPassword());

        if (!expectedTokenSignature.equals(cookieTokenSig)) {
            throw new InvalidCookieException("Cookie token[2] contained signature '" + cookieTokenSig
                    + "' but expected '" + expectedTokenSignature + "'");
        }

        return userDetails;
    }
    
    @Override
    protected String retrieveUserName(Authentication authentication) {
        return ((MyUserAuthentication) authentication.getPrincipal()).getUsername();
    }

    @Override
    protected String retrievePassword(Authentication authentication) {
        return (String) authentication.getCredentials();
    }
}
