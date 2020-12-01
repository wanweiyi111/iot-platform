package com.hzyw.iot.platform.devicemanager.caches;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.platform.models.equip.DeviceAttribute;
import com.hzyw.iot.platform.models.equip.DeviceType;
import com.hzyw.iot.platform.models.equip.Equipment;
import com.hzyw.iot.platform.models.equip.StatusHistory;
import com.hzyw.iot.platform.util.json.MetrxJsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/5.
 */
@Slf4j
@Service
public class StatusHistoryCacheService implements ICacheRead<StatusHistory>, ICacheWrite<StatusHistory> {

    private ConcurrentHashMap<String,StatusHistory> cache;
//    @Autowired
//    private RedisTemplate redisTemplate;

    private static final String CACHE_NAME = "StatusHistoryCache";

    private final EquipmentCacheService deviceCacheService;
    private final AttributeCacheService attributeCacheService;

    public StatusHistoryCacheService(EquipmentCacheService deviceCacheService, AttributeCacheService attributeCacheService) {
        this.cache = new ConcurrentHashMap<>();
        this.deviceCacheService = deviceCacheService;
        this.attributeCacheService = attributeCacheService;
    }

    @Override
//    @Cacheable(cacheManager = "StatusHistoryCache",cacheNames = CACHE_NAME, key = "#deviceId", condition = "#result!=null")
    public StatusHistory get(String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return null;
        }
        if(cache.get(deviceId)==null){
            log.info("Cache--> not found {} in {}", deviceId, CACHE_NAME);
            Equipment equipment = deviceCacheService.get(deviceId);
            if (equipment != null && equipment.getEquipmentType() != null) {
                return new StatusHistory().init(equipment.getEquipmentType(DeviceType.class));
            } else {
                return null;
            }
        }else {
            return cache.get(deviceId);
        }

//        if (equipment != null && equipment.getEquipmentType() != null) {
//            return new StatusHistory().init(equipment.getEquipmentType(DeviceType.class));
//        } else {
//            Object obj = redisTemplate.opsForValue().get(deviceId);
//            StatusHistory his = JSONObject.toJavaObject((JSONObject)obj,StatusHistory.class);
//            return  his;
//            return  null;
//        }
    }

    @Override
//    @CacheEvict(cacheManager = "StatusHistoryCache",cacheNames = CACHE_NAME, key = "#deviceId")
    public StatusHistory remove(String deviceId) {
        StatusHistory his = cache.remove(deviceId);
        log.info("Cache--> evict {} from {}", deviceId, CACHE_NAME);
//        redisTemplate.delete(deviceId);
        return his;
    }

    @Override
    public Boolean evict(String key) {
        cache.remove(key);
        return Objects.isNull(cache.get(key));
    }

    @Override
//    @CachePut(cacheManager = "StatusHistoryCache",cacheNames = CACHE_NAME, key = "#deviceId", condition = "#statusHistory!=null&&#deviceId!=null&&#deviceId!=''")
    public StatusHistory put(String deviceId, StatusHistory statusHistory) {
        log.info("Cache--> put {} into {}", deviceId, CACHE_NAME);
//        redisTemplate.opsForValue().set(deviceId,statusHistory);
        if(statusHistory == null){
            return null;
        }
        StatusHistory his = cache.put(deviceId,statusHistory);
        return his;
    }

    @Override
//    @CacheEvict(cacheManager = "StatusHistoryCache",cacheNames = CACHE_NAME, key = "#deviceId")
    public void delete(String deviceId) {
        cache.remove(deviceId);
        log.debug("Cache--> deleted {} from {}", deviceId, CACHE_NAME);

    }

    @Override
    public void clear(String cacheName) {
        cache.clear();
    }

    public List<MetrxJsonData> saveMetrics(String deviceId, List<MetrxJsonData> array) {
        List<MetrxJsonData> delta = new ArrayList<>();
        StatusHistory statusHistory = get(deviceId);
        if (statusHistory != null) {
            for (MetrxJsonData e : array) {
                DeviceAttribute attribute = attributeCacheService.get(e.getType());
                if (attribute != null) {
                    // 在平台配置范围内的状态数据，才做记录，其他的状态数据一律丢弃。
                    pushValue(statusHistory, attribute, e.getValue());
                    delta.add(e);
                }
            }
        }
        //保存数据
        put(deviceId, statusHistory);
        return delta;
    }

    private void pushValue(StatusHistory statusHistory, DeviceAttribute attribute, Object value) {
        try {
            //String优先，很多情况字符串是最容易格式化的。原则上定义是什么就按什么转。
            if(attribute.getValueType().getName().equals(String.class.getName())){
                statusHistory.push(attribute, value.toString());
            } else if (attribute.getValueType().getName().equals(BigDecimal.class.getName())) {
                statusHistory.push(attribute, new BigDecimal(value.toString()));
            } else if (attribute.getValueType().getName().equals(Boolean.class.getName())) {
                //1,T,True,TRUE,true,
                if (value != null && ("1".equals(value.toString()) || "T".equals(value.toString())||"true".equalsIgnoreCase(value.toString()))) {
                    statusHistory.push(attribute, Boolean.TRUE);
                } else {
                    statusHistory.push(attribute, Boolean.FALSE);
                }
            } else if(attribute.getValueType().getName().equals(Integer.class.getName())){
                statusHistory.push(attribute, new Integer(value.toString()));
            } else if(attribute.getValueType().getName().equals(Arrays.class.getName())){
                //TODO: Arrays 如何处理
                statusHistory.push(attribute, value);
            } else  {
                statusHistory.push(attribute, attribute.getValueType().cast(value));
            }
        } catch (Exception cce) {
            log.error("Value type={} of attribute({}) was not match the Value={}",
                    attribute.getValueType(), attribute.getAttributeKey(), value);
//            statusHistory.push(attribute,value.toString());
        }
    }


}
