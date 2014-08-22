package org.gardella.common.jdbc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;


/**
 * Inserts a well-conformed bean to a given table.
 * 
 * @author Ben
 *
 */
public class BeanPreparedStatementSetter implements PreparedStatementSetter {
    
    private final static Set<Class<?>> SUPPORTED_CLASSES = ImmutableSet.copyOf(new Class<?>[] {
            Boolean.class,
            Boolean.TYPE,
            Byte.class,
            Byte.TYPE,
            Character.class,
            Character.TYPE,
            Double.class,
            Double.TYPE,
            Float.class,
            Float.TYPE,
            Integer.class,
            Integer.TYPE,
            Long.class,
            Long.TYPE,
            Short.class,
            Short.TYPE,
            BigDecimal.class,
            BigInteger.class,
            Date.class,
            java.util.Date.class,
            String.class,
            Timestamp.class
    });

    protected final Log logger = LogFactory.getLog(getClass());
    
    final private String setClause;
    final private Object bean;
    final private SortedMap<String, Method> methodMap;
    final private String tableName;
    final private long id;
    
    public BeanPreparedStatementSetter( String tableName, Object bean) {
        this(tableName, bean, Long.MIN_VALUE);
    }

    
    public BeanPreparedStatementSetter( String tableName, Object bean, long id){
        Preconditions.checkNotNull(tableName);
        Preconditions.checkNotNull(bean);
        this.tableName = tableName;
        this.bean = bean;
        this.id = id;
        StringBuilder values = new StringBuilder();
        
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getMethods();
        methodMap = new TreeMap<String, Method>();
        
        for (Method m: methods) {
            if (isGetter(m)) {
                String mName = underscoreCamelCase(m.getName());
                methodMap.put(mName, m);
            }
        }
        
        values.append(" SET ");        
        for (String s: methodMap.keySet()) {
            values.append(s);
            values.append(" = ?, ");
        }
        values.setLength(values.length() - 2);

        setClause = values.toString();
    }
    
    private boolean isGetter(Method m) {
        int mods = m.getModifiers();
        if (Modifier.isStatic(mods)) {
            return false;
        }
        if (m.getName().equals("getClass")) {
            return false;
        }
        if (m.getParameterTypes().length != 0) {
            return false;
        }
        Class<?> rtype = m.getReturnType();
        if (!SUPPORTED_CLASSES.contains(rtype)) {
            return false;
        }
        String name = m.getName();
        return name.startsWith("get") || name.startsWith("is");
    }
    
    private String underscoreCamelCase( String input ){
        if (input.startsWith("get")) {
            input = input.substring(3);
        } else {
            input = input.substring(2);
        }
        char[] arr = input.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c : arr){
            if(Character.isUpperCase(c) && sb.length() > 0){
                sb.append('_').append(Character.toLowerCase(c));
            }else if(Character.isUpperCase(c) && sb.length() == 0){
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }       
        }
        return sb.toString();
    }
    
    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        Set<String> keySet = methodMap.keySet();
        int counter = 1;
        for (String s: keySet) {
            Method m = methodMap.get(s);
            Object value = ReflectionUtils.invokeMethod(m, this.bean);
            if (value instanceof java.util.Date) {
                java.util.Date d = (java.util.Date)value;
                ps.setObject(counter, new Timestamp(d.getTime()));
            }
            ps.setObject(counter, value);
            counter++;
        }
        if (id != Long.MIN_VALUE) {
            ps.setLong(counter, id);
        }
    }

    public String getInsertSql(){
        return "INSERT INTO " + tableName + setClause;
    }
    
    public String getUpdateSql() {
        return "UPDATE " + tableName + setClause + " WHERE id = ?";
    }

}
