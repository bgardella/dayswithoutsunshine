package phor.uber.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    protected final Log logger = LogFactory.getLog(getClass());
    
    private static final String DATA_URL="http://data.sfgov.org/resource/yitu-d5am.json";
    private static final String ES_URL="http://localhost:9200/locations/sf/";
    private static final String[] STEM_FIELDS = {"actor_1", "actor_2", "actor_3", "title", "locations", "production_company", "distributor", "writer"};
    
    private static String GOOGLE_GEOCODE_URL = "http://maps.google.com/maps/api/geocode/json?sensor=false&address=";
    private static String GEOCODE_SUFFIX = "+San+Francisco+CA";
    
    public static void main(String[] args) {    
        Indexer indexer = new Indexer();
        indexer.beginIndex();
    }

    private String getData() throws HttpException, IOException{
        HttpClient httpClient = new HttpClient();
        GetMethod method = new GetMethod(DATA_URL);
        
        httpClient.executeMethod(method);            
        
        //String charset = method.getResponseCharSet();
        //long leng = method.getResponseContentLength();
        byte[] bodyArr = method.getResponseBody();
                
        //String responseString = IOUtils.readFully(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
        method.releaseConnection();
        
        //BufferedReader theReader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
        //String body = method.getResponseBodyAsString();
        
        return new String(bodyArr);
    }
    
    private void beginIndex(){
        JSONParser parser = new JSONParser();
        JSONArray jarr = new JSONArray();
        
        //String test = "Rainforest Café (145 Jefferson Street)";
        //logger.info("TEST : " + test);
        
        try{
            String responseString = getData();
            jarr = (JSONArray) parser.parse(responseString);
            
            int counter=0;
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
                
                String location = ((String)entry.get("locations"));
                logger.info(location);
                if(location != null){
                    location = location.replaceAll(" ", "+");
                    
                    HttpClient httpClient = new HttpClient();
                    
                    //extract lat/long from google
                    JSONObject googleLatLong = exchangeAddressForCoordinatesByGoogle(httpClient, location, 0);
                    logger.info(location + " : " + googleLatLong.toJSONString());          
                    entry.put("lat", googleLatLong.get("lat"));
                    entry.put("lng", googleLatLong.get("lng"));                    
                    
                    //push to index
                    PutMethod putMethod = new PutMethod(ES_URL+counter);
                    StringRequestEntity body = new StringRequestEntity(entry.toJSONString(), "application/json","UTF-8");
                    putMethod.setRequestEntity(body);
                    int respCode = httpClient.executeMethod(putMethod);
                    putMethod.releaseConnection();
                    
                    logger.info("[" + respCode + "] : " + entry.toJSONString());
                    
                    counter++;
                }
            }         
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    private JSONObject exchangeAddressForCoordinatesByGoogle(HttpClient httpClient, String addressFragment, int count) throws RateLimitException, URISyntaxException{
                
        String url = GOOGLE_GEOCODE_URL + addressFragment + GEOCODE_SUFFIX;
        URI uri = new URI(url);

        String asciiUrl = uri.toASCIIString();
        logger.info(asciiUrl);
        GetMethod method = new GetMethod(asciiUrl);
        
        JSONObject jobj = new JSONObject();
        
        try{
            int respCode = httpClient.executeMethod(method);
            logger.info("RESP CODE: " + respCode);            
            
            String responseString = IOUtils.readFully(new InputStreamReader(method.getResponseBodyAsStream(), "utf-8"));
            //logger.info("RESPONSE: [" + responseString + "]");
            
            method.releaseConnection();
            
            JSONParser parser = new JSONParser();
            
            /******* GOOGLE RESONSE *****/
            jobj = (JSONObject) parser.parse(responseString);
            String status = (String)jobj.get("status");
            if(status.equals("OVER_QUERY_LIMIT")){
                logger.error("RATE LIMIT REACHED. SLEEPING FOR ONE SECOND: [" + addressFragment + "][" + count + "]");
                Thread.sleep(1000);  // sleep for 1 second
                count++;
                if(count > 3){
                    throw new RateLimitException();
                }
                return exchangeAddressForCoordinatesByGoogle(httpClient, addressFragment, count);
            }else{
                JSONArray jarr = (JSONArray)jobj.get("results");
                if(jarr != null && !jarr.isEmpty()){
                    JSONObject jresult = (JSONObject)jarr.get(0);
                    //System.out.println(jresult.toJSONString());
                    JSONObject jLatLong = (JSONObject)((JSONObject)jresult.get("geometry")).get("location");
                    logger.info("LAT: " + jLatLong.get("lat") + " LONG: " + jLatLong.get("lng"));
                    
                    return jLatLong;
                }else{
                    logger.info("NO LOCATION FOR ADDRESS: [" + addressFragment + "]");
                    logger.info(jobj.toJSONString());
                    logger.info("*********************");
                }
            }
            return jobj;

            
        } catch (URIException e) {
            logger.error(e);
            return jobj;
        } catch (HttpException e) {
            logger.error(e);
            return jobj;
        }catch (UnsupportedEncodingException e) {
            logger.error(e);
            return jobj;
        }catch (IOException e){
            logger.error(e);
            return jobj;
        } catch (ParseException e) {
            logger.error(e);
            return jobj;
        } catch (IndexOutOfBoundsException e){
            logger.info(url);
            logger.error(e);
            return jobj;
        } catch (InterruptedException e) {
            logger.error(e);
            return jobj;
        }
    }
    
    
}
