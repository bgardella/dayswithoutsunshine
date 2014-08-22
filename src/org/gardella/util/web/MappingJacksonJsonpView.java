package org.gardella.util.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;


/**
 * Necessary to support JSONP formatting.
 * 
 * @author Ben
 *
 */
public class MappingJacksonJsonpView extends MappingJacksonJsonView {

    private ObjectMapper objectMapper = new ObjectMapper();
    private JsonEncoding encoding = JsonEncoding.UTF8;
    private boolean prefixJson = false;
    private boolean makePretty = false;
    
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Object value = filterModel(model);
        JsonGenerator generator = objectMapper.getJsonFactory().createJsonGenerator(response.getOutputStream(), encoding);
        
        if(this.makePretty){
            generator.useDefaultPrettyPrinter();
        }
        
        String callback = request.getParameter("callback");
        if (callback!=null){
            prefixJson = true;
        }
        if (prefixJson){
            generator.writeRaw(callback + "(");
        }
        objectMapper.writeValue(generator, value);
        generator.flush();

        if (prefixJson){
            generator.writeRaw(");");
            generator.flush();
        }
    }

    public void setMakePretty(boolean makePretty) {
        this.makePretty = makePretty;
    }
    
    
}
