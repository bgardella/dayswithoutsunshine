package org.gardella.web;

import org.gardella.security.dao.UserDAO;
import org.gardella.security.model.User;
import org.gardella.security.model.UserStatus;
import org.gardella.security.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class ValidationController {

    @Autowired
    CryptoService cryptoService;
    @Autowired
    UserDAO userDAO;
    
    @RequestMapping(value="/validate/{cryptoString}", method=RequestMethod.GET)
    public String validate( @PathVariable String cryptoString, Model model ){
        
        boolean valid = cryptoService.isAuthKeyValid(cryptoString);
        model.addAttribute("isValid", valid);
        
        User user = cryptoService.getUserFromAuthKey(cryptoString);
        
        if(user != null && user.getUserStatus() == UserStatus.PENDING){
            user.setUserStatus(UserStatus.ACTIVE);
            userDAO.updateUser(user);
        }
        
        
        return "email-validate";
    }
    
    
    
}
