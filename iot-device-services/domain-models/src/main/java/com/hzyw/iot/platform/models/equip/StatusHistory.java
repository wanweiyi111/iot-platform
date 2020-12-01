package com.hzyw.iot.platform.models.equip;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;


/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/14.
 */
public class StatusHistory implements Serializable {

    /**
     * 单个属性的历史状态值的缓存队列长度
     */
    private static int size = 10;
    private Map<DeviceAttribute, LinkedList> cache = new HashMap<>();

    /**
     * 初始化缓存
     *
     * @param type DeviceType设备类型
     */
    public StatusHistory init(DeviceType type) {
        type.getAttributes().values().stream().forEach(e -> {
            if (DeviceAttribute.STRING.equals(e.getValueType())) {
                cache.put(e, new LinkedList<String>());
            } else if (DeviceAttribute.CHAR.equals(e.getValueType())) {
                cache.put(e, new LinkedList<Integer>());
            } else if (DeviceAttribute.INT.equals(e.getValueType())) {
                cache.put(e, new LinkedList<Integer>());
            } else if (DeviceAttribute.NUMBER.equals(e.getValueType())) {
                cache.put(e, new LinkedList<BigDecimal>());
            } else if (DeviceAttribute.ARRAY.equals(e.getValueType())) {
                cache.put(e, new LinkedList<Arrays>());
            } else if (DeviceAttribute.BOOLEAN.equals(e.getValueType())) {
                cache.put(e, new LinkedList<Boolean>());
            } else {
                cache.put(e, new LinkedList());
            }
        });
        return this;
    }

    /**
     * 将最新的状态数据存入缓存
     *
     * @param key   DeviceAttribute
     * @param value T
     */
    public <T> void push(DeviceAttribute<T> key, T value) {
        if (key.getValueType().isInstance(value)) {
            LinkedList<T> list = cache.get(key);
            if (list != null) {
                if (list.size() == size) {
                    list.poll();        //缓存队列满,移除第一个数据
                }
                list.offer(value);
            }
        }
    }
//            log.errorString.format("require type is %s", key.getValueType().getName()));

    /**
     * 获取指定的状态值
     *
     * @param key
     * @return
     */
    public <T> T getLastAttribute(DeviceAttribute<T> key) {
        if (cache.get(key) != null) {
            return (T) cache.get(key).peekLast();
        }
        return null;
    }

    /**
     * 获取最近的所有状态数据
     *
     * @return 属性对象集合Map<DeviceAttribute, Object>
     */
    public Map<DeviceAttribute, Object> getLastAttributes() {
        Map<DeviceAttribute, Object> m = new HashMap<>(cache.keySet().size());
        cache.keySet().stream().forEach(e -> {
            m.put(e, cache.get(e).peekLast());
        });
        return m;
    }

    /**
     * 获取最近的所有状态数据
     *
     * @return 简单值集合Map<String, Object>
     */
    public Map<String, Object> getLastAttributesSimple() {
        Map<String, Object> m = new HashMap<>(cache.keySet().size());
        cache.keySet().stream().forEach(e -> {
            m.put(e.getAttributeKey(), cache.get(e).peekLast());
        });
        return m;
    }
    /**
     * 获取最近的所有状态数据
     *
     * @return 简单值集合Map<String, String>
     */
    public Map<String, String> getLastAttributesString() {
        Map<String, String> m = new HashMap<>(cache.keySet().size());
        cache.keySet().stream().forEach(e -> {
            m.put(e.getAttributeKey(), String.valueOf(cache.get(e).peekLast()));
        });
        return m;
    }

    /**
     * 按指定的key获取缓存的数据（最后10次,index1~10按记录时间顺序，index最大为最新数据。）
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> List<T> getAttributeCache(DeviceAttribute<T> key) {
        if (cache.get(key) != null) {
            List<T> list = cache.get(key);
            return list;
        }
        return null;
    }
}
