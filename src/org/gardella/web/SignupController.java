package org.gardella.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gardella.security.dao.UserDAO;
import org.gardella.security.model.User;
import org.gardella.security.model.UserStatus;
import org.gardella.security.model.UserType;
import org.gardella.security.service.CryptoService;
import org.gardella.security.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;


@SessionAttributes({"signupInfo"})
@Controller
public class SignupController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private Pattern emailPattern;
    
    @Autowired
    UserDAO userDAO;
    @Autowired
    EmailService emailService;
    @Autowired
    CryptoService cryptoService;
    
    @ModelAttribute("signupInfo")
    public SignupInfo createSignupInfo() {
        SignupInfo info = new SignupInfo();
        return info;
    }
    
    public SignupController(){
        emailPattern = Pattern.compile(EMAIL_PATTERN);
    }
    
    @RequestMapping(value="/signup")
    public String renderSignup( Model model ){
        
        model.addAttribute("signupInfo", createSignupInfo());
        
        return "signup";
    }
    
    @RequestMapping(value="/signup/submit", method=RequestMethod.POST)
    public String submitSignup( @ModelAttribute("signupInfo") SignupInfo signupInfo, Model model, HttpServletRequest request ){
        
        if( !validateEmail(signupInfo.getEmail()) ){
            model.addAttribute("signupFail", Boolean.valueOf(true));
            model.addAttribute("reason", "Email is invalid. Try Again.");
            return "signup-fail";
        }
        if( isEmailTaken(signupInfo.getEmail()) ){
            model.addAttribute("signupFail", Boolean.valueOf(true));
            model.addAttribute("reason", "Email is taken. Try Again.");
            return "signup-fail";
        }
        
        User user = savePendingAccount(signupInfo);
        
        String host = "gardella.org";
        int port = 80;
        String ctx = request.getContextPath();
        if(port != 80){
            host+=":"+port;
        }
        host+=ctx;
        
        sendAuthEmail(user, host);
        
        
        model.addAttribute("emailFail", Boolean.valueOf(false));
        return "signup-auth";
    }
    
    private boolean isEmailTaken( String email ){
        
        return userDAO.isEmailTaken(email);
    }
    private boolean validateEmail( String email ) {

        //validate email
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()){
            return false;
        }
        return true;
    }
    
    private User savePendingAccount(SignupInfo signupInfo){
        
        User user = new User();
        user.setEmail(signupInfo.getEmail());
        user.setPassword(DigestUtils.md5Hex(signupInfo.getClearPassword()) );
        user.setUserStatus(UserStatus.PENDING);
        user.setUserType(UserType.USER);
        user.setCell(signupInfo.getCell());
        
        return userDAO.insertUser(user);
    }
    
    private void sendAuthEmail(User user, String host) {
        try {
            
            String key = cryptoService.generateAuthKey(user.getEmail(), user.getPassword());

            String emailContent = "<html><body></p><p>Thank you for joining GARDELLA.ORG.</p><p>To activate your account, click: " +
                    "<a href=\"http://"+host+"/validate/"+key+"\">" +
                    "http://"+host+"/validate/"+key+"</a>" +
                    "<p>This link will be valid for 24 hours.</p>" +
                    "</body></html>";


            emailService.sendOutboundHTMLEmail(user.getEmail(), "Welcome to GARDELLA.ORG!", emailContent);
        } catch (Throwable t) {
            logger.error("Could not send confirmation email to: " + user.getEmail());
        }
    }
}
