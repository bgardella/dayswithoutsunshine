package org.gardella.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gardella.security.model.User;

import com.techventus.server.voice.Voice;
import com.techventus.server.voice.datatypes.records.SMS;
import com.techventus.server.voice.datatypes.records.SMSThread;

public class SMSService {

    protected final Log logger = LogFactory.getLog(getClass());
    
    private String gvoiceNumber;
    private String gvoiceUser;
    private String gvoicePass;
    
    public void sendWord(User user, String word) throws IOException{
        
        Voice voice = new Voice(gvoiceUser, gvoicePass);
        voice.sendSMS(user.getCell(), word );
    }
    
    public void checkForUserResponse(User user) throws IOException{
        
        Voice voice = new Voice(gvoiceUser, gvoicePass);
        Collection<SMSThread> threads = voice.getSMSThreads();

        for(SMSThread t : threads){
            
            if(!t.getRead()){
            Collection<SMS> mlist = t.getAllSMS();
            for(SMS m : mlist){
                String number = m.getFrom().getNumber();
                if(number.equals(user.getCell())){
                    String msg = m.getContent();
                    Date date = m.getDateTime();
                    System.out.println(date.toString() + " -- message: [" + msg + "] from: [" + number + "]");
                }
             }
            }
        }
    }

    public void setGvoiceNumber(String gvoiceNumber) {
        this.gvoiceNumber = gvoiceNumber;
    }

    public void setGvoiceUser(String gvoiceUser) {
        this.gvoiceUser = gvoiceUser;
    }

    public void setGvoicePass(String gvoicePass) {
        this.gvoicePass = gvoicePass;
    }
    
    
    
}
