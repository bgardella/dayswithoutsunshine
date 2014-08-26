package phor.uber.web;

import java.io.IOException;
import java.io.InputStreamReader;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class SearchController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    private HttpClient httpClient = new HttpClient();
    private static final String AC_QUERY = "http://localhost:9200/locations/_suggest";
    
    @RequestMapping(value="/autocomplete/{stem}", method=RequestMethod.GET)
    public void autocomplete( @PathVariable String stem, HttpServletResponse resp ){
        
        PostMethod method = new PostMethod(AC_QUERY);
        
        String json = createJsonBody(stem);
        
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
        }
        
/*
        {
            "loc_suggest":{
                "text":"to",
                "completion": {
                    "field" : "auto_complete"
                }
            }
        }
*/        
    }
    
    private String createJsonBody(String stem){
        JSONObject jobj1 = new JSONObject();
        jobj1.put("text", stem);
        
        JSONObject jobj2 = new JSONObject();
        jobj2.put("field", "auto_complete");
        
        jobj1.put("completion", jobj2);
        
        JSONObject jobj = new JSONObject();
        jobj.put("suggest", jobj1);
        
        return jobj.toJSONString();
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
}
