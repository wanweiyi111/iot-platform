package com.hzyw.iot.platform.devicemanager.caches;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hzyw.iot.platform.models.transfer.DefaultDeviceException;
import com.hzyw.iot.platform.models.transfer.DeviceServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/19.
 */

@Slf4j
@Service
public class TransactionSessionCacheService implements ICacheLock<DeviceServiceContext>, ICacheRead<DeviceServiceContext> {
//    private static Map<String, DeviceServiceContext> transactionMap = new HashMap<>();

    private static final String CACHE_NAME = "TransactionCache";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public DeviceServiceContext get(String key) {
        Object result = redisTemplate.opsForValue().get(key);
        String deviceTypeJson = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        return JSONObject.parseObject(deviceTypeJson, DeviceServiceContext.class);
    }

    @Override
    @Deprecated
    public Boolean evict(String key) {
//        DeviceServiceContext context =get(key);
        return  redisTemplate.delete(key);
//        if(redisTemplate.delete(key)){
//            return boolean;
//        }else {
//            log.error("remove key:{} from TransactionCache failed",key);
//            return context;
//        }
    }

    @Override
    public boolean tryLock(String key, DeviceServiceContext context, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(context.getTransactionId(), context, timeout, unit);
    }

    @Override
    public boolean release(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public boolean refresh(String key, long timeout, TimeUnit unit) {
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.expire(key, timeout, unit);
        }
        return false;
    }
}
