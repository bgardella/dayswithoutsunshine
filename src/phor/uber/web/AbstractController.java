package phor.uber.web;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gardella.util.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

public abstract class AbstractController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    private HttpClient httpClient = new HttpClient();
    
    protected String sendElasticSearch(String url, String json, Model model){
        
        PostMethod method = new PostMethod(url);
        
        try {
            StringRequestEntity requestEntity = new StringRequestEntity(json,"application/json","UTF-8");
            method.setRequestEntity(requestEntity);                                                
            int statusCode = httpClient.executeMethod(method);
            if(statusCode == HttpStatus.SC_OK){
                
                String responseString = IOUtils.readFully(new InputStreamReader(method.getResponseBodyAsStream(), "utf-8"));
                JSONParser parser = new JSONParser();
                JSONObject data = (JSONObject) parser.parse(responseString);
                
                returnJsonSuccess(data, model);
            }
        } catch (Exception e) {
           returnJsonFail(e, model);
        } finally{
            method.releaseConnection();
        }
        
        return "";
    }
    
    protected String sendElasticSearch(String url, String json, HttpServletResponse resp){
        
        PostMethod method = new PostMethod(url);
        
        try {
            StringRequestEntity requestEntity = new StringRequestEntity(json,"application/json","UTF-8");
            method.setRequestEntity(requestEntity);                                                
            int statusCode = httpClient.executeMethod(method);
            if(statusCode == HttpStatus.SC_OK){
                
                String responseString = IOUtils.readFully(new InputStreamReader(method.getResponseBodyAsStream(), "utf-8"));
                JSONParser parser = new JSONParser();
                JSONObject data = (JSONObject) parser.parse(responseString);
                
                returnJsonSuccess(data, resp);
            }
        } catch (Exception e) {
           returnJsonFail(e, resp);
        } finally{
            method.releaseConnection();
        }
        
        return "";
    }
    
    private void returnJsonSuccess(JSONObject data,  Model model){
        JSONObject wrapper = new JSONObject();
        wrapper.put("result", "SUCCESS");
        wrapper.put("data", data);
        
        model.addAttribute("data", data);
    }
    
    
    private void returnJsonFail(Exception ex,  Model model){
        JSONObject wrapper = new JSONObject();
        wrapper.put("result", "FAIL");
        
        JSONObject data = new JSONObject();
        data.put("excepton", ex.getClass().toString());
        data.put("message", ex.getMessage());
        //e.getStackTrace();
        
        model.addAttribute("data", data);
    }
    
    protected void returnJsonSuccess(JSONObject data,  HttpServletResponse response){
        JSONObject wrapper = new JSONObject();
        wrapper.put("result", "SUCCESS");
        wrapper.put("data", data);
        
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        try {
            response.getWriter().write(wrapper.toJSONString());
        } catch (IOException e) {
            logger.error("cannot write json response", e);
        }
    }
    
    
    protected void returnJsonFail(Exception ex,  HttpServletResponse response){
        JSONObject wrapper = new JSONObject();
        wrapper.put("result", "FAIL");
        
        JSONObject data = new JSONObject();
        data.put("excepton", ex.getClass().toString());
        data.put("message", ex.getMessage());
        //e.getStackTrace();
        
        wrapper.put("data", data);
        
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        try {
            response.getWriter().write(wrapper.toJSONString());
        } catch (IOException e) {
            logger.error("cannot write json response", e);
        }
    }
    
    protected String generateUrlBase(HttpServletRequest req) {
        
        StringBuilder buf = new StringBuilder(64);
        buf.append(req.getScheme())
            .append("://")
            .append(req.getServerName());

        int port = req.getServerPort();

        if (!req.isSecure() && port != 80) {
            buf.append(":").append(port);
        }

        if (StringUtils.hasText(req.getContextPath())) {
            // append the context path 
            buf.append(req.getContextPath());
        }

        return buf.toString();
    }
    
    protected void setFrontEndVariables(Model model, HttpServletRequest req){
        String urlBase = generateUrlBase(req);
        model.addAttribute("urlBase", urlBase);
    }
    
    
}
