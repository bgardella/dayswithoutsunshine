package org.gardella.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gardella.security.MyUserAuthentication;
import org.gardella.security.model.UserStatus;
import org.gardella.security.util.SecurityContextUtils;
import org.gardella.service.NLPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class DefaultController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private NLPService nlpService;
    
    
    @RequestMapping(value="/")
    public String renderRoot( Model model, HttpServletRequest request ){
        
        MyUserAuthentication auth = SecurityContextUtils.currentUserDetails();
        if(auth.getStatus() == UserStatus.ACTIVE){
         
            return "home";
        }
        return "redirect:/login";
    }
    
    @RequestMapping(value="/login")
    public String renderLogin( Model model, HttpServletRequest request ){
         
        return "login";
    }
    
    @RequestMapping(value="/home")
    public String renderHome( Model model, HttpServletRequest request ){
        
        MyUserAuthentication auth = SecurityContextUtils.currentUserDetails();
        if(auth.getStatus() == UserStatus.ACTIVE){
         
            nlpService.parseText();
            
            return "home";
        }
        return "redirect:/login";
    }
    
}
