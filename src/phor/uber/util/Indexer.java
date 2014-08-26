package phor.uber.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.gardella.util.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * CLI for indexing json datasource to ElasticSearch
 * 
 * 
 */
public class Indexer {

    private static final String DATA_URL="http://data.sfgov.org/resource/yitu-d5am.json";
    
    private static final String ES_URL="http://localhost:9200/locations/sf/";
    
    private static final String[] STEM_FIELDS = {"actor_1", "actor_2", "actor_3", "title", "locations", "production_company", "distributor", "writer"};
    
    public static void main(String[] args) {
    
        JSONParser parser = new JSONParser();
        HttpClient httpClient = new HttpClient();
        GetMethod method = new GetMethod(DATA_URL);
        
        JSONArray jarr = new JSONArray();
        
        try{
            httpClient.executeMethod(method);            
            String responseString = IOUtils.readFully(new InputStreamReader(method.getResponseBodyAsStream(), "utf-8"));
            
            jarr = (JSONArray) parser.parse(responseString);
            
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int counter=0;
        try {
            
            
            for(Object o : jarr){
                JSONObject entry = (JSONObject)o;

                //gather up stems for autocomplete
                JSONObject jobj = new JSONObject();
                JSONArray jsArr = new JSONArray();
                
                for(int i=0;i<STEM_FIELDS.length; i++){
                    if(entry.containsKey(STEM_FIELDS[i])){
                        jsArr.add(entry.get(STEM_FIELDS[i]));
                    }
                }
                
                jobj.put("input", jsArr);
                entry.put("auto_complete", jobj);                 
            
                
                
                //push to index
                PutMethod putMethod = new PutMethod(ES_URL+counter);
                StringRequestEntity body = new StringRequestEntity(entry.toJSONString(), "application/json","UTF-8");
                putMethod.setRequestEntity(body);
                int respCode = httpClient.executeMethod(putMethod);
                
                System.out.println("[" + respCode + "] : " + entry.toJSONString());
                
                counter++;
            }         
            
            
            
        } catch (URIException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        
    }
    
    
    
    
}
