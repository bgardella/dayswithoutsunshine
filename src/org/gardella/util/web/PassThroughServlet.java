/**
 * 
 */
package org.gardella.util.web;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

/**
 * This servlet is for handling files that have no business
 * being served up by tomcat, but because the spring servlet-mapping
 * can get hairy, this was the easiest workaround.  Obviously, this won't 
 * be needed when apache is fronting, but what the hey.
 * 
 * @author Ben
 *
 */
public class PassThroughServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException {
        
        ServletContext servletContext = getServletContext();

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        
        int p = 0;
        
        if(uri.startsWith("/a/")){  //supports deployment under subdirectory: "/a/**"
            p = uri.indexOf('/', 3);
        }else{
            p = uri.indexOf('/', 1); 
        }
        
        if (p < 0) {
            resp.setStatus(404);
            return;
        }
        
        InputStream is;
        
        String contextPathMatch = uri.substring(0, p);
        if(contextPath.length() > 0 && contextPath.equals(contextPathMatch)){
        
            String resourcePath = uri.substring(p);
            is = servletContext.getResourceAsStream(resourcePath);
            if (is == null) {
                resp.setStatus(404);
                return;
            }
            
        }else if(contextPath.length() == 0){ //supports root context apps
            is = servletContext.getResourceAsStream(uri);
        }else{
            resp.setStatus(404);
            return;
        }
        
        resp.setContentType( servletContext.getMimeType(req.getRequestURI()) );
        
        OutputStream out = resp.getOutputStream();
        try {
            ByteStreams.copy(is, out);
        } finally {
            Closeables.closeQuietly(is);
            // The servlet container should close the response for us.
        }
    }

}