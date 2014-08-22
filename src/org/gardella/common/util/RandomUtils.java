package org.gardella.common.util;

import java.util.ArrayList;
import java.util.List;

public class RandomUtils {

    
    /**
     * Fetch a random number set of a given length
     * @return
     */
    public static List<Long> getRandomNumberSet(int numberLength){
        
        List<Long> list = new ArrayList<Long>(numberLength);
        
        return populateRandomNumberSet(list, numberLength);
    }
        
    private static List<Long> populateRandomNumberSet(List<Long> list, int maxLength){   
        
        if(list.size() == maxLength)return list;
        
        double d = Math.random() * (maxLength*2);
        long el = Math.round(d);

        if(el == maxLength*2){
                el = maxLength-1;
        }
        
        if(!list.contains(el)){
                list.add(el);
        }
        if(list.size() == maxLength)return list;
        
        
        long sr = el >> 1;
        if(sr < maxLength && !list.contains(sr)){
                list.add(sr);
                if(list.size() == maxLength)return list;
        }
        
        long sl = el << 1;
        if(sl < maxLength && !list.contains(sl)){
                list.add(sl);
                if(list.size() == maxLength)return list;
        }
        
        if(list.size() < maxLength){
            populateRandomNumberSet(list, maxLength);
        }
        
        return list;
    }
    
}
