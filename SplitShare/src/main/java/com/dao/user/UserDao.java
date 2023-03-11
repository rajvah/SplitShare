package com.dao.user;

import com.models.user.Users;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;

/**
 * @author Harshit Rajvaidya
 */
public class UserDao extends AbstractDAO<Users> {

    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Users find(String user_id){
        try{
            return get(user_id);
        }catch (Exception e){
            // :todo log the error
            return null;
        }

    }

    public Users save(Users model){
        try{
            return persist(model);
        } catch(Exception e){
            // :todo log the error
            return null;
        }

    }

    public List<Users> findAll() {

        try{
            return list(namedTypedQuery("com.models.user.Users.findAll"));
        } catch (Exception e){
            // :todo log the error
            return null;
        }
    }

    public Users usersModelFromEmail(String email){

        Query<Users> q = namedTypedQuery("validateUserWithEmail");
        q = q.setParameter("email", email);
        try{
            return q.getSingleResult();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void deleteUser(Users user){
        currentSession().delete(user);
    }
}
