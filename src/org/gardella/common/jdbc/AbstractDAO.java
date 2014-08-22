package org.gardella.common.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class AbstractDAO {

    protected final Log logger = LogFactory.getLog(getClass());
    

    protected DataSource dataSource;    
    protected JdbcTemplate template;

    public AbstractDAO() {
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
    }


    protected <T> Iterable<T> iterable(String sql, RowMapper<T> rowMapper, Object... params) {
        return iterable(sql, rowMapper, Arrays.asList(params));
    }

    protected <T> Iterable<T> iterable(String sql, Class<T> cls, List<Object> params) {
        return iterable(sql, new BeanPropertyRowMapper<T>(cls), params);
    }
    
    protected <T> Iterable<T> iterable(String sql, Class<T> cls, Object... params) {
        return iterable(sql, new BeanPropertyRowMapper<T>(cls), Arrays.asList(params));
    }

    /**
     * Performs a database query for a single object, returning null if the
     * object does not exist in the database.
     * 
     * <p>If you'd rather throw an exception when the object is not found,
     * use {@link JdbcTemplate#queryForObject(String, Class, Object...)}.
     * 
     * @param <T> the type of object to return
     * @param sql   the SQL to execute
     * @param cls   the type of object to return
     * @param params   the parameters for the SQL query
     * @return   the found object, or null if no rows were returned by the
     *    query
     */
    protected <T> T queryForObject(String sql, Class<T> cls, Object... params) {
        try {
            return template.queryForObject(sql, params, new BeanPropertyRowMapper<T>(cls));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Simple list query with class/bean declaration.  Use this if you want to fetch 
     * a simple set of objects from the db if the table is mapped simply to an object.
     * If the result set is expected to be rather large ( >1000 ), then use
     * iterable(...) instead of this one.  Will be more efficient memory-wise.  This one
     * is good for the simple day-to-day stuff. 
     * 
     * @param <T>
     * @param sql
     * @param cls
     * @param params
     * @return List of result objects
     */
    protected <T> List<T> queryForList(String sql, Class<T> cls, Object... params) {
        return template.query( sql, params, new BeanPropertyRowMapper<T>(cls));
    }
    
    /**
     * Fetches a list of simple Longs.
     * 
     */
    protected List<Long> queryForListOfLong(String sql, Object[] params) {
        return template.queryForList( sql, params, Long.class );
    }
    
    
    /**
     * Can be used for updates, inserts or deletes.
     * 
     * @param sql
     * @param params
     * @return int the number of rows affected
     */
    protected int update( String sql, Object... params ){
        
        int rowsAffected = template.update(sql, params);
        if( rowsAffected == 0 ){
            throw new ZeroRowsAffectedException("No rows affected for sql: [" + sql + "]");
        }
            
        return rowsAffected;
    }
    
    /**
     * Insert all the properties of a given bean
     * 
     * @param tableName
     * @param bean
     * @return int the number of rows affected
     */
    protected int insertBean( String tableName, Object bean ){
        BeanPreparedStatementSetter setter = new BeanPreparedStatementSetter( tableName, bean );
        return template.update(setter.getInsertSql(), setter );
    }

    /**
     * Returns the next ID from the global sequence.
     * 
     * @return
     */
    public long nextId() {
        // TODO: There are transaction concerns here -- 
        // during a transaction, you generally don't want to roll back sequence
        // values to prevent ID clobbers after a rollback.
        String sql = null;
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtils.getConnection(template.getDataSource());
            sql = "UPDATE seq_num SET pk_next_id=LAST_INSERT_ID(pk_next_id+1)  where pk_name='global_id'";
            ps1 = conn.prepareStatement(sql);
            ps1.execute();
            sql = "SELECT LAST_INSERT_ID() - 1 AS ID";
            ps2 = conn.prepareStatement(sql);
            rs = ps2.executeQuery();
            if (rs.next()) {
                return rs.getLong("ID");
            }
            throw new IllegalStateException("Could not get ID from sequence.");
        } catch (SQLException e) {
            SQLExceptionTranslator translator = template.getExceptionTranslator();
            throw translator.translate("Fetching ID from sequence.", sql, e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps1);
            JdbcUtils.closeStatement(ps2);
            DataSourceUtils.releaseConnection(conn, template.getDataSource());
        }
    }

    
    /**
     * For sql queries that expect a 0 or a 1 response.
     * 
     * @param sql
     * @param params
     * @return
     */
    protected boolean isTrue( String sql, Object... params ){
        return (template.queryForInt(sql, params) == 0) ? false : true;
    }

    protected String queryForString( String sql, Object... params ){
        return template.queryForObject(sql, String.class, params);
    }
    
    protected Long queryForLong( String sql, Object... params ){
        try{
            return template.queryForObject(sql, Long.class, params);
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }

    protected int queryForInt( String sql, Object... params ){
        try{
            return template.queryForObject(sql, Integer.class, params);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    protected Float queryForFloat( String sql, String colName , Object... params){
        SqlRowSet set = template.queryForRowSet(sql, params);
        set.first();
        return set.getFloat( colName );
    }

}
