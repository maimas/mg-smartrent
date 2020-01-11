package com.mg.persistence.service.nosql;


import com.mg.persistence.service.QueryService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoQueryService<T> implements QueryService<T> {

    private MongoTemplate mongoTemplate;

    public MongoQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public T findOneBy(String fieldName, Object value, Class<T> entityClass) {
        List<T> results = findAllBy(fieldName, value, entityClass);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<T> findAllBy(String fieldName, Object value, Class<T> entityClass) {
        Query query = new Query().addCriteria(Criteria.where(fieldName).is(value));

        return mongoTemplate.find(query, entityClass, entityClass.getSimpleName());
    }

    public List<T> findAll(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass, entityClass.getSimpleName());
    }

    public T save(T model) {
        return mongoTemplate.save(model, model.getClass().getSimpleName());
    }

    public void save(List<T> models) {
        models.forEach(this::save);
    }
}
