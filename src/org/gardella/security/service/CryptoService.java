package org.gardella.security.service;

import java.security.Key;
import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gardella.security.dao.UserDAO;
import org.gardella.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;



public class CryptoService {

    private static final String ALGORITHM = "AES";
    private static final byte[] SECRET = new byte[] { 'B', 'l', '0', 'W', 'M', '3', 'D', '0', 'w', 'n', 'W', 'i', 'g', 'g', 'l', '3' };
    
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    UserDAO userDAO;
    
    /**
     * Keys are valid for 24 hours.
     * 
     * @param authkey
     * @return
     */
    public boolean isAuthKeyValid(String authkey){
        try{
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decodeBase64(authkey.getBytes());
            byte[] decValue = c.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            
            logger.info("decrypted: " + decryptedValue);
            
            String email = decryptedValue.substring( decryptedValue.indexOf('<', 0)+1, decryptedValue.indexOf('>', 0) );
            String md5pass = decryptedValue.substring( decryptedValue.indexOf('[', 0)+1, decryptedValue.indexOf(']', 0) );
            
            User user = userDAO.getUser(email);
            if(user != null && user.getPassword().equals(md5pass)){
                
                String timekey = decryptedValue.substring( decryptedValue.indexOf('{', 0)+1, decryptedValue.indexOf('}', 0) );
                String timeKeyNow = generateTimeKey();
                if(timekey.equals(timeKeyNow)){
                    return true;
                }
                String timeKeyTomorrow = generateTimeKeyTomorrow();
                if(timekey.equals(timeKeyTomorrow)){
                    return true;
                }
            }
            
        } catch (Exception e) {
            logger.error(e);
        }
        
        return false;
    }
    
    /**
     * It only returns a user if the key is still valid
     * 
     * @param authkey
     * @return
     */
    public User getUserFromAuthKey(String authkey){
        try{
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decodeBase64(authkey.getBytes());
            byte[] decValue = c.doFinal(decordedValue);
            String decryptedValue = new String(decValue);
            
            logger.info("decrypted: " + decryptedValue);
            
            String email = decryptedValue.substring( decryptedValue.indexOf('<', 0)+1, decryptedValue.indexOf('>', 0) );
            String md5pass = decryptedValue.substring( decryptedValue.indexOf('[', 0)+1, decryptedValue.indexOf(']', 0) );
            
            User user = userDAO.getUser(email);
            if(user != null && user.getPassword().equals(md5pass)){
                
                String timekey = decryptedValue.substring( decryptedValue.indexOf('{', 0)+1, decryptedValue.indexOf('}', 0) );
                String timeKeyNow = generateTimeKey();
                if(timekey.equals(timeKeyNow)){
                    return user;
                }
                String timeKeyTomorrow = generateTimeKeyTomorrow();
                if(timekey.equals(timeKeyTomorrow)){
                    return user;
                }
            }
            
        } catch (Exception e) {
            logger.error(e);
        }
        
        return null;
    }
    
    
    public String generateAuthKey( String email, String md5Pass ){
        
        String timekey = generateTimeKey();

        String tempStr  =   "<" + email   + ">" +
                            "[" + md5Pass    + "]" +
                            "{" + timekey + "}" ;

        logger.info( tempStr );
        
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encValue = c.doFinal(tempStr.getBytes());
            byte[] cryptVal = Base64.encodeBase64URLSafe(encValue);
            return new String(cryptVal);
            
        } catch (Exception e) {
            logger.error(e);
            return "";
        }
    }
    
    private Key generateKey() throws Exception {
        Key key = new SecretKeySpec(SECRET, ALGORITHM);
        return key;
    }
    
    private String generateTimeKey(){
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); //must add leading zero if needed later
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR); 
       
        String timekey = ((dayOfMonth < 10) ? "0"+dayOfMonth+"" : dayOfMonth+"") + dayOfYear+"";
        
        return timekey;
    }
    
    private String generateTimeKeyTomorrow(){
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); //must add leading zero if needed later
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR); 
         
        String timekey = ((dayOfMonth < 10) ? "0"+dayOfMonth+"" : dayOfMonth+"") + dayOfYear+"";
          
        return timekey;
    }
}
