package com.hzyw.iot.platform.devicemanager.caches;

import java.util.Objects;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/2.
 */
public interface ICacheRead<T> {

    /**
     * 从缓存中获取对应的键值对，如果缓存中没有，则查询数据库。
     *
     * @param key
     * @return
     */
   T get(String key);

    /**
     * 从缓存中获取对应的键值对，如果缓存中没有，则查询数据库，为空返回自定义数据，（注意：自定义数据不进缓存）。
     *
     * @param key
     * @param defaultValue
     * @return
     */
    default T getNonNull(String key, T defaultValue) {
        if (Objects.isNull(get(key))) {
            return defaultValue;
        } else {
            return get(key);
        }
    }

    /**
     * 刷新缓存，清除原有key并从新加载
     *
     * @param key
     * @return
     */
    default T refresh(String key) {
        evict(key);
        return get(key);
    }

    /**
     * 从缓存中剔出对应的键值对，并返回被剔除的值（按实现需求可选择更新数据库）。
     *
     * @param key
     * @return T
     */
    default T remove(String key){
        T t = get(key);
        if(t!=null){
            evict(key);
        }
        return t;
    }

    /**
     * 从缓存中剔出对应的键值对，并返回被剔除的值（注意：此方法只做缓存操作，不更新数据库）。
     *
     * @param key
     */
    Boolean evict (String key);
}
