/*package com.hzyw.iot.util;
 
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


@Service
public class BaseMongoDB {
    *//**
     * MongoTemplate
     *//*
    @Autowired
    @Qualifier(value = "primaryMongoTemplate")
    private MongoTemplate mongoTemplate;

    *//**
     * 创建索引
     * 只包含单键和复合索引
     *
     * @param collectionName 集合名称
     * @param fieldName      字段名称
     *//*
    public void ensureIndex(String collectionName, String... fieldName) {
        IndexOperations indexOperations = mongoTemplate.indexOps(collectionName);
        Index index = null;
        if (fieldName.length == 1) {
            index = new Index();
            index.on(fieldName[0], Sort.Direction.DESC);
        } else {
            BasicDBObject dbObject = new BasicDBObject();
            for (String aFieldName : fieldName) {
                //复合索引 1dec -1asc
                dbObject.put(aFieldName, 1);
            }
            index = new CompoundIndexDefinition(dbObject);
        }
        indexOperations.ensureIndex(index);
    }

    *//**
     * 增加一条数据并返回
     *
     * @param tableName
     * @param params
     * @return
     *//*
    public Map<String, Object> save(String tableName, Map<String, Object> params) {
        BasicDBObject val = new BasicDBObject(params);
        //val.put(Constant.SORT_NAME, new Date());
        mongoTemplate.insert(val, tableName);
        id2Id(val);
        return val.toMap();
    }

    *//**
     * insertDBObjectList
     *
     * @param tableName
     * @param dbObjects
     *//*
    public void insertDBObjectList(String tableName, List<DBObject> dbObjects) {
        mongoTemplate.insert(dbObjects, tableName);
        Update update = new Update();
        //update.currentDate(Constant.SORT_NAME);
        mongoTemplate.updateMulti(new Query(), update, tableName);
    }

    *//**
     * findShowFields
     *
     * @param tableName
     * @param query
     * @param fields
     * @param skip
     * @param limit
     * @return
     *//*
    public DBCursor findShowFields(String tableName, DBObject query, DBObject fields, int skip, int limit) {
        DBCursor resultList = mongoTemplate.getCollection(tableName).find(query, fields).skip(skip).limit(limit);
        return resultList;
    }

    *//**
     * 批量删除
     *
     * @param tableName
     * @param ids
     *//*
    public void deleteByIds(String tableName, String... ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(convertObjectId(ids)));
        mongoTemplate.remove(query, tableName);
    }

    *//**
     * deleteByQuery
     *
     * @param tableName
     * @param query
     *//*
    public void deleteByQuery(String tableName, Query query) {
        mongoTemplate.remove(query, tableName);
    }

    *//**
     * 清除表数据
     *
     * @param tableName
     *//*
    public void deleteAll(String tableName) {
        Query query = new Query();
        mongoTemplate.remove(query, tableName);
    }

    *//**
     * 更新一条数据
     *
     * @param query
     * @param update
     * @param tableName
     *//*
    public void updateFirst(Query query, Update update, String tableName) {
        mongoTemplate.updateFirst(query, update, tableName);
    }

    *//**
     * 更新n条数据
     *
     * @param query
     * @param update
     * @param tableName
     *//*
    public void updateMulti(Query query, Update update, String tableName) {
        mongoTemplate.updateMulti(query, update, tableName);
    }

    *//**
     * 获取表中数据量
     *
     * @param tableName
     * @return
     *//*
    public long count(String tableName) {
        return mongoTemplate.count(null, tableName);
    }

    *//**
     * 获取表中数据量
     *
     * @param query
     * @param tableName
     * @return
     *//*
    public long count(Query query, String tableName) {
        return mongoTemplate.count(query, tableName);
    }

    *//**
     * 获取数据列表
     *
     * @param query
     * @param tableName
     * @return
     *//*
    public List<DBObject> getResultList(Query query, String tableName, int skip, int limit) {
        List<DBObject> list = new ArrayList<>();
        mongoTemplate.executeQuery(query.skip(skip).limit(limit), tableName, new DocumentCallbackHandler() {
            @Override
            public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
                id2Id(dbObject);
                toChangeBigDecimal(dbObject);
                list.add(dbObject);
            }
        });
        return list;
    }

    *//**
     * getCount
     *
     * @param tableName
     * @param query
     * @param criteria
     * @return
     *//*
    public long getCount(String tableName, DBObject query, Criteria criteria) {
        BasicQuery basicQuery = new BasicQuery(query);
        Query queryAll = basicQuery.addCriteria(criteria);
        return mongoTemplate.count(queryAll, tableName);
    }

    *//**
     * getCount
     *
     * @param tableName
     * @param query
     * @return
     *//*
    public long getCount(String tableName, DBObject query) {
        Query queryTemp = new Query();
        generatorLikeQuery(query.toMap(), queryTemp);
        return mongoTemplate.count(queryTemp, tableName);
    }

    *//**
     * getList
     *
     * @param tableName
     * @param query
     * @param skip
     * @param limit
     * @return
     *//*
    public List<DBObject> getList(String tableName, Query query, int skip, int limit) {
        query.skip(skip);
        query.addCriteria(Criteria.where("_id").ne(null));
        query.limit(limit);
        return mongoTemplate.find(query, DBObject.class, tableName);
    }

    *//**
     * getList
     *
     * @param tableName
     * @param query
     * @param skip
     * @param limit
     * @return
     *//*
    public List<DBObject> getList(String tableName, DBObject query, int skip, int limit) {
        List<DBObject> list = new ArrayList<>();
        DBCursor dbCursor = mongoTemplate.getCollection(tableName).find(query);
        dbCursor.skip(skip);
        dbCursor.limit(limit);
        while (dbCursor.hasNext()) {
            addList(list, dbCursor);
        }
        dbCursor.close();
        return list;
    }

    *//**
     * 用于特殊查询比如模糊查询
     *
     * @param tableName
     * @param query
     * @return
     *//*
    public LinkedList<DBObject> getList(String tableName, Query query) {
        LinkedList<DBObject> list = new LinkedList<>();

        mongoTemplate.executeQuery(query, tableName, dbObject -> {
//            toChangeBigDecimal(dbObject);
//            id2Id(dbObject);
            list.add(dbObject);
        });
        return list;
    }

    *//**
     * addList
     *
     * @param list
     * @param dbCursor
     *//*
    private void addList(List list, DBCursor dbCursor) {
        DBObject next = dbCursor.next();
        ObjectId id = (ObjectId) next.get("_id");
        next.put("_id", id.toString());
        list.add(next);
    }

    *//**
     * 转换为ObjectId
     *
     * @param ids
     * @return
     *//*
    public ObjectId[] convertObjectId(String... ids) {
        if (ids == null) {
            return new ObjectId[0];
        }
        ObjectId[] objectIds = new ObjectId[ids.length];

        if (ids == null || ids.length == 0) {
            return objectIds;
        }

        for (int i = 0; i < ids.length; i++) {
            objectIds[i] = new ObjectId(ids[i]);
        }
        return objectIds;
    }

    *//**
     * 整理dbObject(转化ObjectId为String)
     *
     * @param dbObject
     *//*
    public void id2Id(DBObject dbObject) {
        ObjectId objectId = (ObjectId) dbObject.get("_id");
        dbObject.put("_id", objectId.toString());
    }

    *//**
     * setPageable
     *
     * @param pageNum
     * @param pageSize
     * @param sort
     * @return
     *//*
    public Pageable setPageable(int pageNum, int pageSize, Sort sort) {
        Pageable pageable = new PageRequest(pageNum, pageSize, sort);
        return pageable;
    }

    *//**
     * existTable
     *
     * @param table
     * @return
     *//*
    public boolean existTable(String table) {
        return mongoTemplate.collectionExists(table);
    }

    *//**
     *//*
    public List<DBObject> findParamsMap(String tableName, DBObject dbObject) {
        DBCursor dbObjects = mongoTemplate.execute(tableName, dbCollection -> dbCollection.find(dbObject));
        List<DBObject> dbObjectList = new ArrayList<>();
        dbObjects.forEach(dbObject1 -> {
            final ObjectId id = (ObjectId) dbObject1.get("_id");
            dbObject1.put("_id", id.toString());
            dbObjectList.add(dbObject1);
        });
        return dbObjectList;
    }

    *//**
     * 根据Query查询结果集
     *
     * @param tableName
     * @param query
     * @return
     *//*
    public List<DBObject> findByQuery(String tableName, Query query) {
        List<DBObject> returnList = new ArrayList<>();
        mongoTemplate.executeQuery(query, tableName, dbObject -> {
            toChangeBigDecimal(dbObject);
            returnList.add(dbObject);
        });
        return returnList;
    }

    *//**
     * 根据Query查询结果集
     *
     * @param tableName
     * @param query
     * @return
     *//*
    public List<DBObject> findByAggregate(String tableName, Query query) {
        List<DBObject> returnList = new ArrayList<>();
        mongoTemplate.executeQuery(query, tableName, dbObject -> {
            if (dbObject.get("_id") instanceof LinkedHashMap) {
                for (Object id : ((LinkedHashMap) dbObject.get("_id")).keySet()) {
                    dbObject.put(id.toString(),((LinkedHashMap) dbObject.get("_id")).get(id));
                }
            }
            dbObject.removeField("_id");
            toChangeBigDecimal(dbObject);
            returnList.add(dbObject);
        });
        return returnList;
    }


    *//**
     * 删除字段
     *
     * @param dbObject
     * @param field
     * @return
     *//*
    public DBObject removeField(DBObject dbObject, String field) {
        Set<String> set = dbObject.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String fieldInDb = iterator.next();
            if (field.equals(fieldInDb)) {
                iterator.remove();
            }
        }
        return dbObject;
    }

    *//**
     * id查询
     *
     * @param tableName
     * @param id
     * @return
     *//*
    public DBObject getPartsMsg(String tableName, String id) {
        ObjectId objectId = new ObjectId(id);
        DBObject dbObject = mongoTemplate.execute(tableName, dbCollection -> dbCollection.findOne(objectId));
        return dbObject;
    }

    *//**
     * 根据配件id获取配件信息
     *
     * @param tableName
     * @param ids
     * @return
     *//*
    public List<DBObject> queryByIds(String tableName, String... ids) {
        List<DBObject> returnValue = new ArrayList<>();
        Query query = new Query();
        if (ids.length == 0) {
            return null;
        }
        query.addCriteria(Criteria.where("_id").in(ids));
        mongoTemplate.executeQuery(query, tableName, dbObject -> {
            returnValue.add(dbObject);
        });
        return returnValue;
    }

    *//**
     * 清除全部
     * 慎用
     *//*
    public void clearCollections(String tableName) {
        mongoTemplate.dropCollection(tableName);
    }

    *//**
     * 保存ListMap
     *
     * @param tableName
     * @param params
     *//*
    public void saveAll(String tableName, List<LinkedHashMap<String, Object>> params) {
        int size = params.size();
        if (size >= 50000) {
            double i = size / 10000.0;
            int j = 0;
            for (; j < ((int) i); j++) {
                mongoTemplate.insert(mapToDBObject(params.subList(j * 10000, (j + 1) * 10000)), tableName);
            }
            mongoTemplate.insert(mapToDBObject(params.subList(j * 10000, (int) (j * 10000 + (i - j) * 10000))),
                tableName);
        } else {
            mongoTemplate.insert(mapToDBObject(params), tableName);
        }
    }

    *//**
     * 映射关系
     **//*
    private List<DBObject> mapToDBObject(List<LinkedHashMap<String, Object>> params) {
        List<DBObject> dbObjects = new ArrayList<>();
        params.forEach(map -> {
            dbObjects.add(new BasicDBObject(map));
        });
        return dbObjects;
    }

    *//**
     * 保存ListMap
     *
     * @param tableName
     * @param params
     *//*
    public void backUp(String tableName, JSONArray params) {
        List<DBObject> dbObjects = new ArrayList();
        for (int i = 0; i < params.size(); i++) {
            JSONObject jsonObject = params.getJSONObject(i);
            DBObject dbObject = new BasicDBObject(jsonObject);
            Object id = dbObject.get("_id");
            ObjectId objectId = new ObjectId(id.toString());
            ((BasicDBObject) dbObject).put("_id", objectId);
            //((BasicDBObject) dbObject).append(Constant.SORT_NAME, new Date());
            dbObjects.add(dbObject);
        }
        mongoTemplate.insert(dbObjects, tableName);
    }

    *//**
     * @return
     *//*
    public List<DBObject> pageMongo(Criteria criteria, String tableName,
                                    Map<String, Object> paramFind, Integer pageNo, Integer pageSize) {
        List<DBObject> returnList = new ArrayList<>();
        Query query = new Query();
        generatorLikeQuery(paramFind, query);
        query.skip(pageNo);
        query.limit(pageSize);
        Sort.Direction desc = Sort.Direction.DESC;
        //Sort sort = new Sort(desc, Constant.SORT_NAME);
        //query.with(sort);
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        mongoTemplate.executeQuery(query, tableName, dbObject -> {
            toChangeBigDecimal(dbObject);
            id2Id(dbObject);
            returnList.add(dbObject);
        });
        return returnList;
    }

    *//**
     * toChangeBigDecimal
     *
     * @param dbObject
     *//*
    private void toChangeBigDecimal(DBObject dbObject) {
        Set<String> keys = dbObject.keySet();
        for (String key : keys) {
            Object value = dbObject.get(key);
            if (value instanceof Decimal128) {
                value = ((Decimal128) value).bigDecimalValue().toPlainString();
            }
            dbObject.put(key, value);
        }
    }

    *//**
     * 处理mongodb查询建立regex 模糊查询的query
     *
     * @param paramFind
     * @param query
     *//*
    public void generatorLikeQuery(Map<String, Object> paramFind, Query query) {
        if (paramFind != null && !paramFind.isEmpty()) {
            Iterator<String> iterator = paramFind.keySet().iterator();
            Criteria criteria = new Criteria();
            while (iterator.hasNext()) {
                String next = iterator.next();
                Object value = paramFind.get(next);
                if (value instanceof String) {
                    //_ {} $  ^
                    value = filterRegex(((String) value));
                    query.addCriteria(criteria.where("_id").ne(null)
                        .and(next).regex(Pattern.compile("^.*" + value + ".*$")));
                } else {
                    query.addCriteria(criteria.where("_id").ne(null).and(next).is(value));
                }
            }

        }
    }

    *//**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     *//*
    public String filterRegex(String keyword) {
        if (keyword!=null) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    *//**
     * distinctQuery
     *
     * @param collectionName
     * @param criteria
     * @param key
     * @return
     *//*
    public List<DBObject> distinctQuery(String collectionName, Criteria criteria, String key) {
        BasicQuery basicQuery = new BasicQuery(new BasicDBObject());
        basicQuery.addCriteria(criteria);
        DBObject queryObject = basicQuery.getQueryObject();
        List distinct = mongoTemplate.getCollection(collectionName).distinct(key, queryObject);
        List<DBObject> returnList = new ArrayList<>();
        distinct.forEach(value -> {
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put(key, value);
            returnList.add(dbObject);
        });
        return returnList;
    }


    *//**
     * aggregate
     *
     * @param agg
     * @param collectionName
     * @param dbObjectClass
     * @return
     *//*
    public AggregationResults<DBObject> aggregate(Aggregation agg, String collectionName,
                                                  Class<DBObject> dbObjectClass) {
        return mongoTemplate.aggregate(agg, collectionName, dbObjectClass);
    }


    *//**
     * 判断字段是否存在
     *//*
    public boolean existsField(String collectionName, String fieldName) {
        DBObject dbObject = new BasicDBObject();
        DBObject dbObjectParam = new BasicDBObject();
        ((BasicDBObject) dbObjectParam).put("$exists", true);
        ((BasicDBObject) dbObject).put(fieldName, dbObjectParam);
        int count = mongoTemplate.getCollection(collectionName).find(dbObject).count();
        return count > 0;
    }

 

    *//**
     * 修改字段名称
     *
     * @param paramMap key是以前的字段名称，value是之后修改的字段名称
     *//*
    public void updateFields(String collectionName, Map<String, String> paramMap) {
        Update update = new Update();
        Set<String> keySet = paramMap.keySet();
        for (String key : keySet) {
            if (!existsField(collectionName, paramMap.get(key))) {
                update.rename(key, paramMap.get(key));
            }
        }
        if (!update.toString().equals("{ }")) {
            mongoTemplate.updateMulti(new Query(), update, collectionName);
        }
    }
} */