package com.hzyw.iot.service.Impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.service.ObjectService;
import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import com.mongodb.client.result.DeleteResult;

/**
 * 设备接入：设备信息定义
 *
 */
@Service
public class ObjectServiceImpl implements ObjectService{
	@Autowired
	@Qualifier(value = "primaryMongoTemplate") //primarymongotemplate为默认的mongotemplate
	private MongoTemplate mongoTemplate;
	
	private static final String collection_object = "object";
	private static final String collection_unit = "unit";
	  
	 
	/* 
	 * ID存在则更新，不存在则插入
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.ObjectService#saveObjects(java.util.List)
	 */
	public void saveObjects(List<JSONObject> jsonParam) {
		for (int i = 0; i < jsonParam.size(); i++) {
			if(jsonParam.get(i).containsKey("_id")){
				Update update = new Update();
				//存在ID则修改
			    /*mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(jsonParam.get(i).get("_id"))), 
					new Update().set("date", "2015-08-08"), collection_object);*/   //只修改一个字段
				//BasicDBObject basicDBObject = new BasicDBObject();
				//BasicDBObject fields = new BasicDBObject();
				Iterator iter = jsonParam.get(i).entrySet().iterator();
		        while (iter.hasNext()){
		            Map.Entry entry = (Map.Entry) iter.next();
		            //表中不存在的字段会自动新插入，存在则修改? 不影响表中原有字段
		            if(!entry.getKey().toString().equals("_id")){
		             //basicDBObject.put("$set", new BasicDBObject(entry.getKey().toString(), entry.getValue()));
		             //fields.put(entry.getKey().toString(), entry.getValue());
		             update.set(entry.getKey().toString(), entry.getValue());
		            }
		        } 
		       //basicDBObject.put("$set", new BasicDBObject(fields));
		       // Document document = Document.parse(json);
				mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(jsonParam.get(i).get("_id"))), update, collection_object);
			}else{
				mongoTemplate.save(jsonParam.get(i), collection_object);
			}
		}
	}
	
	
	/* 
	 * 根据ID删除
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.ObjectService#removeById(java.lang.String)
	 */
	public void removeById(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, collection_object);
    } 
	
	/* 
	 * 根据对象删除，符合条件的全部删除
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.ObjectService#removeByObject(com.alibaba.fastjson.JSONObject)
	 */
	public Object removeByObject(JSONObject object ){ 
		ArrayList<Criteria> list=new ArrayList<Criteria>();
        Iterator iter = object.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            list.add(Criteria.where(entry.getKey().toString()).is(entry.getValue()));
        } 
        Criteria[] arr = new Criteria[list.size()];
        list.toArray(arr);
        Criteria criteria = new Criteria().andOperator(arr);
        Query query=new Query(criteria);
 		DeleteResult rs = mongoTemplate.remove(query, collection_object);
		return rs;
    }
	
	/* 
	 * 查询列表，支持分页
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.ObjectService#findObjectByPage(com.alibaba.fastjson.JSONObject)
	 */
	public List<JSONObject> findObjectByPage(JSONObject para ) {
        ArrayList<Criteria> list=new ArrayList<Criteria>();
        Iterator iter = para.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            list.add(Criteria.where(entry.getKey().toString()).is(entry.getValue() ));
        } 
        if (para.get("sn") != null){
            //模糊查询
            list.add(Criteria.where("sn").regex(".*?\\" +para.get("sn")+ ".*"));
        }
        
        Criteria[] arr = new Criteria[list.size()];
        list.toArray(arr);
        Criteria criteria = new Criteria().andOperator(arr);
        Query query=new Query(criteria);
        //query.skip((vo.getPage()-1)*vo.getPageSize());
        //query.limit(10); 
		//Query query = new Query(Criteria.where("plc_id").is(plcId.get("plcId")));
		List<JSONObject> listJson = mongoTemplate.find(query, JSONObject.class, collection_object);
		return listJson;
	}
	   
	/* 
	 * 查询列表
	 * (non-Javadoc)
	 * @see com.hzyw.iot.service.ObjectService#findObject(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public List<JSONObject> findObjects(JSONObject para) {
        ArrayList<Criteria> list=new ArrayList<Criteria>();
        Iterator iter = para.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            list.add(Criteria.where(entry.getKey().toString()).is(entry.getValue()));
        } 
        if (para.get("sn") != null){
            //模糊查询
            list.add(Criteria.where("sn").regex(".*?\\" +para.get("sn")+ ".*"));
        }
        
        Criteria[] arr = new Criteria[list.size()];
        list.toArray(arr);
        Criteria criteria = new Criteria().andOperator(arr);
        Query query=new Query(criteria);
        //query.skip((vo.getPage()-1)*vo.getPageSize());
        //query.limit(10); 
		//Query query = new Query(Criteria.where("plc_id").is(plcId.get("plcId")));
        
		List<JSONObject> listJson = mongoTemplate.find(query, JSONObject.class,collection_object);
		for(JSONObject obj : listJson){
			String id = obj.get("_id").toString();
			obj.remove("_id");
			obj.put("_id", id);
		}
		return listJson;
	}


	@Override
	public void saveUnits(List<JSONObject> jsonParam) {
		for (int i = 0; i < jsonParam.size(); i++) {
			if(jsonParam.get(i).containsKey("_id")){
				Update update = new Update();
				//存在ID则修改
			    /*mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(jsonParam.get(i).get("_id"))), 
					new Update().set("date", "2015-08-08"), collection_object);*/   //只修改一个字段
				BasicDBObject basicDBObject = new BasicDBObject();
				BasicDBObject fields = new BasicDBObject();
				Iterator iter = jsonParam.get(i).entrySet().iterator();
		        while (iter.hasNext()){
		            Map.Entry entry = (Map.Entry) iter.next();
		            //表中不存在的字段会自动新插入，存在则修改? 不影响表中原有字段
		            if(!entry.getKey().toString().equals("_id")){
		             //basicDBObject.put("$set", new BasicDBObject(entry.getKey().toString(), entry.getValue()));
		             fields.put(entry.getKey().toString(), entry.getValue());
		             update.set(entry.getKey().toString(), entry.getValue());
		            }
		        } 
		        //basicDBObject.put("$set", new BasicDBObject(fields));
		        //Update update = new BasicUpdate(basicDBObject);
		         
				mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(jsonParam.get(i).get("_id"))), update, collection_object);
			}else{
				mongoTemplate.save(jsonParam.get(i), collection_unit);
			}
		}
		
	}


	@Override
	public List<JSONObject> findUnits(JSONObject para) {

        ArrayList<Criteria> list=new ArrayList<Criteria>();
        Iterator iter = para.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            list.add(Criteria.where(entry.getKey().toString()).is(entry.getValue()));
        } 
        
        Criteria[] arr = new Criteria[list.size()];
        list.toArray(arr);
        Criteria criteria = new Criteria().andOperator(arr);
        Query query=new Query(criteria);
        //query.skip((vo.getPage()-1)*vo.getPageSize());
        //query.limit(10); 
		//Query query = new Query(Criteria.where("plc_id").is(plcId.get("plcId")));
        
		List<JSONObject> listJson = mongoTemplate.find(query, JSONObject.class,collection_unit);
		for(JSONObject obj : listJson){
			String id = obj.get("_id").toString();
			obj.remove("_id");
			obj.put("_id", id);
		}
		return listJson;
	
	}
 

}
