package org.gardella.security.dao;

import org.gardella.common.jdbc.AbstractDAO;
import org.gardella.common.jdbc.ZeroRowsAffectedException;
import org.gardella.security.model.LoginSession;
import org.gardella.security.model.User;


public class UserDAO extends AbstractDAO {

    

    public void deleteLoginSession(String sessionKey){
        String sql = "DELETE FROM user_session WHERE session_key = ?";
        Object[] params = {sessionKey};
        update( sql, params );
    }
    public LoginSession getLoginSession(String sessionKey){
        String sql = "SELECT * FROM user_session WHERE session_key = ?";
        Object[] params = {sessionKey};
        return queryForObject(sql, LoginSession.class, params);
    }

    public void createLoginSession(LoginSession sess){
        String sql = "INSERT INTO user_session ( session_key, user_id, created_on, expired_on ) VALUES( ?,?,?,?)";
        Object[] params = {sess.getSessionKey(), sess.getUserId(), sess.getCreatedOn(), sess.getExpiredOn()};
        update( sql, params );
    }
    
    public User getUser(long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        return queryForObject(sql, User.class, id);
    }
    
    public User getUser(String email){
        String sql = "SELECT * FROM user WHERE email = ?";
        return queryForObject(sql, User.class, email);
    }
    
    public User updateUser( User user ){
        String sql ="UPDATE user set email=?, password=?, cell=?, user_type=?, user_status=? where id=?";
        Object[] params = {user.getEmail(), user.getPassword(), user.getCell(), user.getUserType().name(), user.getUserStatus().name(), user.getId()};
        update( sql, params );
        return user;
    }

    public User insertUser(User user) {
        user.setId(nextId());
        String sql = "INSERT INTO user (id, email, password, cell, user_type, user_status) VALUES( ?,?,?,?,?,? )";
        Object[] params = {user.getId(), user.getEmail(), user.getPassword(), user.getCell(), user.getUserType().name(), user.getUserStatus().name()};
        update(sql, params);
        return user;
    }
    
    public void deleteUser(User user) {
        String sql = "DELETE FROM user WHERE id = ?";
        int rows = update(sql, user.getId());
        if (rows != 1) {
            throw new ZeroRowsAffectedException("No such user: " + user);
        }
    }
    
    public boolean isEmailTaken(String email){
        String sql = "SELECT id from user where email = ?";
        Object[] params = {email};
        Long id = queryForLong(sql, params);
        return (id > 0L)? true : false;
    }
    
    public boolean isCellTaken(String cell){
        String sql = "SELECT id from user where cell = ?";
        Object[] params = {cell};
        Long id = queryForLong(sql, params);
        return (id > 0L)? true : false;
    }
    
}
