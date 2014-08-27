package phor.uber.web;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for all RESTful search queries done by the Front End.
 * 
 * @author Ben
 *
 */
@Controller
public class SearchController extends AbstractController{

    protected final Log logger = LogFactory.getLog(getClass());
    
    private static final String AC_QUERY = "http://localhost:9200/locations/_suggest";
    private static final String SEARCH_QUERY = "http://localhost:9200/locations/_search";
    private static final String BYID_QUERY = "http://localhost:9200/locations/sf/";
    
    private static final String[] STEM_FIELDS = {"actor_1", "actor_2", "actor_3", "title", "locations", "production_company", "distributor", "writer", "director"};
    
    @RequestMapping(value={"/","/index.html","/index.htm"}, method=RequestMethod.GET)
    public String home( HttpServletRequest req, Model model ){
        setFrontEndVariables(model, req);
        return "home";
    }
    
    
    @RequestMapping(value="/byid/{id}", method=RequestMethod.GET)
    public String byId( @PathVariable long id, Model model ){
        
        return sendElasticSearch(BYID_QUERY+id, model);
    }
    
    @RequestMapping(value="/autocomplete/{stem}", method=RequestMethod.GET)
    public String autocomplete( @PathVariable String stem, Model model ){
        
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
        
        JSONObject jobj1 = new JSONObject();
        jobj1.put("text", stem);
        
        JSONObject jobj2 = new JSONObject();
        jobj2.put("field", "auto_complete");
        
        jobj1.put("completion", jobj2);
        
        JSONObject jobj = new JSONObject();
        jobj.put("suggest", jobj1);
        
        return sendElasticSearch(AC_QUERY, jobj.toJSONString(), model);
    }
    
    @RequestMapping(value="/exactSearch", method=RequestMethod.POST)
    public void explicitSearch( @RequestParam("search") String searchString, HttpServletResponse resp){
                        
        /*
         {
            "query" : {
                "query_string" : {
                    "query": "Valencia St. from 16th to 17th",
                    "default_operator" : "AND"
                }
            }
        } 
         */
        
        JSONObject jobj2 = new JSONObject();
        jobj2.put("query", searchString);
        jobj2.put("default_operator", "AND");
        
        JSONObject jobj1 = new JSONObject();
        jobj1.put("query_string", jobj2);
        
        JSONObject jobj = new JSONObject();
        jobj.put("query", jobj1);
        
        
        sendElasticSearch(SEARCH_QUERY, jobj.toJSONString(), resp);
    }
    
    
    @RequestMapping(value="/looseSearch", method=RequestMethod.POST)
    public void looseSearch( @RequestParam("search") String searchString, HttpServletResponse resp ){
                        
        /*
         {
            "query" : {
                "query_string" : {
                    "fields" : ["actor_1", "actor_2", "actor_3", "title", "locations", "production_company", "distributor", "writer"],
                    "query": "Valencia St. from 16th to 17th",
                }
            }
        } 
         */
        JSONArray fieldArr = new JSONArray();
        fieldArr.addAll(Arrays.asList(STEM_FIELDS));
        
        JSONObject jobj2 = new JSONObject();
        jobj2.put("query", searchString);
        jobj2.put("fields", fieldArr);
        
        JSONObject jobj1 = new JSONObject();
        jobj1.put("query_string", jobj2);
        
        JSONObject jobj = new JSONObject();
        jobj.put("query", jobj1);
        
        
        sendElasticSearch(SEARCH_QUERY, jobj.toJSONString(), resp);
    }
    
    
    
    
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    

}
