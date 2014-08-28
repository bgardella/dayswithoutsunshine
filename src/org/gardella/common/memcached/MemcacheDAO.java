package org.gardella.common.memcached;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

public class MemcacheDAO implements DisposableBean {

    protected final Log logger = LogFactory.getLog(getClass());
    
    
    private MemcachedClientIF client;
    private int timeoutSeconds = 60 * 60;

    public void setAddresses(String addresses) {
        try {
            logger.info("setting memcache address: " + addresses);
            client = new MemcachedClient(AddrUtil.getAddresses(addresses));
            logger.info("memcache client intialized");
        } catch (IOException e) {
            logger.error("fail to init memcache client", e);
            throw new IllegalStateException(e);
        }
    }
    
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public String get(String key) {
        // Try to get a value, for up to 5 seconds, and cancel if it doesn't return
        Future<Object> f = client.asyncGet(key);
        try {
            return (String)f.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Since we don't need this, go ahead and cancel the operation.  This
            // is not strictly necessary, but it'll save some work on the server.
            f.cancel(false);
            throw new IllegalStateException(e);
        }
    }
    
    private static void verifyExpireSeconds(int expireSeconds) {
        if (expireSeconds <= 0 || expireSeconds >= 60*60*24*30) {  //cannot be longer than 30 days
            throw new IllegalArgumentException("Bad expireSeconds " + expireSeconds);
        }
    }

    /**
     * "set" destroys whatever is already in the cache
     *      
     */
    public Future<Boolean> set(String key, int expireSeconds, String value) {
        verifyExpireSeconds(expireSeconds);
        Future<Boolean> future = client.set(key, expireSeconds, value);
        return future;
    }

    /**
     * "add" will fail if the key is already present.
     * 
     */
    public Future<Boolean> add(String key, int expireSeconds, String value) {
        verifyExpireSeconds(expireSeconds);
        Future<Boolean> future = client.add(key, expireSeconds, value);
        return future;
    }
    
    /**
     * removes a key from the cache.
     * 
     */
    public Future<Boolean> delete(String key){
        return client.delete(key);
    }
    
    
    @Override
    public void destroy() {
        client.shutdown(5, TimeUnit.MINUTES);
    }
}
