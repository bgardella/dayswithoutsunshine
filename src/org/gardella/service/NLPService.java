package org.gardella.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;


/**
 * My Natural Language Processing experiements
 * 
 * http://opennlp.apache.org/
 * 
 * @author Ben
 *
 */
public class NLPService {

    protected final Log logger = LogFactory.getLog(getClass());
    
    @Value("${text.directory}")
    private String textDirectory;
   
    private NameFinderME nameFinder;
    private SentenceDetectorME sentenceDetector;
    private POSTaggerME posTagger;
    

    private void init(){
        try{
            InputStream is = new FileInputStream(textDirectory+"/bin/en-ner-person.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(is);
            this.nameFinder = new NameFinderME(model);
            
            File sentenceBinFile = new File(textDirectory+"/bin/en-sent.bin");
            FileInputStream fis = new FileInputStream(sentenceBinFile);
            SentenceModel smodel = new SentenceModel(fis);
            this.sentenceDetector = new SentenceDetectorME(smodel);
            
            POSModel posModel = new POSModelLoader().load(new File(textDirectory+"/bin/en-pos-maxent.bin"));
            this.posTagger = new POSTaggerME(posModel);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public void parseText(){
        
        if(this.nameFinder == null){
            init();
        }
        
        File directory = new File(textDirectory);
        
        try {
            File[] allFiles = directory.listFiles();
            StringBuilder sb = new StringBuilder();
            
            for(File f : allFiles){
                if(!f.isDirectory()){
                    FileInputStream fis = new FileInputStream(f);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while(( line = br.readLine()) != null ) {
                       sb.append(line).append(" ");
                    }
                }
            }

            String[] sentences = this.sentenceDetector.sentDetect(sb.toString());
            
            Map<String,List<POS>> sentMap = new HashMap<String,List<POS>>();
            Map<POS,List<String>> posMap = new HashMap<POS,List<String>>();
            
            for(String sent : sentences){
                logger.info("~~~~~~~~~~~~~~");
                logger.info(sent);
                String[] tokens = parsePartsOfSpeech(sent, sentMap, posMap);
                //nameFinder(tokens);
            }
            
            logger.info("GENERATE RANDOM SENTENCES FOLLOWING PATTERN....");
            
            Set<String> set = sentMap.keySet();
            for(String sentence : set){
                List<POS> posList = sentMap.get(sentence);
                logger.info("SOURCE SENTENCE: " + sentence + " : ");
                StringBuffer newSB = new StringBuffer();
                for(POS pos : posList){
                    List<String> wordList = posMap.get(pos);
                    int wordIdx = randomBetween( 0, wordList.size());
                    String word = wordList.get(wordIdx);
                    newSB.append(word).append(" ");
                }
                logger.info("RANDOM SENTENCE: " + newSB.toString());                
            }
            
            
            
            
            
            //PRP + VB + NN
/*            
            List<String> properNouns = posMap.get(POS.PRP);
            List<String> verbs = posMap.get(POS.VB);
            List<String> nouns = posMap.get(POS.NN);
            
            int prpIdx = randomBetween( 0, properNouns.size());
            int vbIdx = randomBetween( 0, verbs.size());
            int nnIdx = randomBetween( 0, nouns.size());
            
            StringBuffer sbx = new StringBuffer();
            
            sbx.append(properNouns.get(prpIdx)).append(" ");
            sbx.append(verbs.get(vbIdx)).append(" ");
            sbx.append(nouns.get(nnIdx));
            
            
            logger.info("*********************");
            logger.info(sbx.toString());
            logger.info("*********************");
            */
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private int randomBetween(int low, int high){
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }
    
    private String[] parsePartsOfSpeech(String sent, Map<String,List<POS>> sentMap, Map<POS,List<String>> posMap){
        
        String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(sent);
        String[] tags = posTagger.tag(tokens);
        
        POSSample sample = new POSSample(tokens, tags);
        logger.info(sample.toString());
        
        logger.info("token list length: " + tokens.length);
        logger.info("tag list length: " + tags.length);
        
        List<POS> posList = new ArrayList<POS>();
        for(int i=0; i< tokens.length; i++){
            try{
                POS pos = POS.valueOf(tags[i]);
                posList.add(pos);
                List<String> words = null;
                if(posMap.containsKey(pos)){
                    words = posMap.get(pos);                    
                }else{
                    words = new ArrayList<String>();
                    posMap.put(pos, words);
                }
                words.add(tokens[i]);
                
            }catch(IllegalArgumentException e){}
        }
        sentMap.put(sent, posList);
        
        return tokens;
    }
    
    private void chunker(String[] tokens, String[] tags){
        try{
            InputStream is = new FileInputStream(textDirectory+"/bin/en-chunker.bin");
            ChunkerModel cModel = new ChunkerModel(is);
            
            ChunkerME chunkerME = new ChunkerME(cModel);
            String[] chunks = chunkerME.chunk(tokens, tags);
            logger.info("CHUNKS...");
            for(String ch : chunks){
                logger.info(ch);
            }
            
            Span[] spans = chunkerME.chunkAsSpans(tokens, tags);
            logger.info("SPANS...");
            for(Span sp : spans){
                logger.info(sp.toString());
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void nameFinder(String[] tokens){
        Span[] spanArr = nameFinder.find(tokens);
        logger.info("names found: " + spanArr.length);
        for(Span s : spanArr){
            logger.info(s.toString());
        }
    }
    
}
