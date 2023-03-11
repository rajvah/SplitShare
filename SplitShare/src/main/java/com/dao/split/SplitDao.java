package com.dao.split;

import com.models.split.Splits;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.UUID;

/**
 * @author Harshit Rajvaidya
 */
public class SplitDao extends AbstractDAO<Splits> {
    public SplitDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Splits find(UUID split_id){
        return get(split_id);
    }

    public Splits save(Splits model){
        try{
            return persist(model);
        } catch (Exception e){
            return null;
        }

    }
    public Splits update(Splits model){
        Splits split = this.currentSession().load(Splits.class, model.getSplit_id());
        split.setAmount(model.getAmount());
        this.currentSession().update(split);
        return split;
    }
}
