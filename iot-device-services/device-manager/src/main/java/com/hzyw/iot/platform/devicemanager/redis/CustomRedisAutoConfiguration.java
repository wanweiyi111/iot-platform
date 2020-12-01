package com.hzyw.iot.platform.devicemanager.redis;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAttributeDO;
import com.hzyw.iot.platform.models.equip.DeviceAttribute;
import com.hzyw.iot.platform.models.equip.DeviceType;
import com.hzyw.iot.platform.models.equip.Equipment;
import com.hzyw.iot.platform.models.equip.StatusHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 初始化 redis 相关
 * <pre>
 *     support spring-data-redis 1.8.10.RELEASE
 *     not support spring-data-redis 2.0.0.RELEASE +
 * </pre>
 *
 * @author Created by YL
 * @blame Android Team
 */
@Configuration
//@ConditionalOnProperty(name = "spring.redis.host")
@EnableConfigurationProperties(RedisProperties.class)
//@EnableAutoConfiguration(exclude = {RedisConnectionFactory.class})
@EnableCaching(proxyTargetClass = true) // 加上这个注解是为了支持 @Cacheable、@CachePut、@CacheEvict 等缓存注解
@Slf4j
public class CustomRedisAutoConfiguration {

    /**
     * key serializer
     */
    static final StringRedisSerializer STRING_REDIS_SERIALIZER = new StringRedisSerializer();
    static final RedisSerializationContext.SerializationPair<String> STRING_PAIR = RedisSerializationContext
            .SerializationPair.fromSerializer(STRING_REDIS_SERIALIZER);
    /**
     * value serializer pair
     */
    static final GenericFastJsonRedisSerializer GENERIC_FAST_JSON_REDIS_SERIALIZER = new GenericFastJsonRedisSerializer();
    static final RedisSerializationContext.SerializationPair<Object> GENERIC_FAST_JSON_PAIR = RedisSerializationContext
            .SerializationPair.fromSerializer(GENERIC_FAST_JSON_REDIS_SERIALIZER);

//    private final RedisConnectionFactory redisConnectionFactory;
//
//    CustomRedisAutoConfiguration(RedisConnectionFactory redisConnectionFactory) {
//        this.redisConnectionFactory = redisConnectionFactory;
//    }

    /**
     * 配置 RedisTemplate，设置序列化器
     * <pre>
     *     在类里面配置 RestTemplate，需要配置 key 和 value 的序列化类。
     *     key 序列化使用 StringRedisSerializer, 不配置的话，key 会出现乱码。
     * </pre>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置key序列化类，否则key前面会多了一些乱码
        template.setKeySerializer(STRING_REDIS_SERIALIZER);
        template.setHashKeySerializer(STRING_REDIS_SERIALIZER);
        // fastjson serializer
        template.setValueSerializer(GENERIC_FAST_JSON_REDIS_SERIALIZER);
        template.setHashValueSerializer(GENERIC_FAST_JSON_REDIS_SERIALIZER);
        // 如果 KeySerializer 或者 ValueSerializer 没有配置，则对应的 KeySerializer、ValueSerializer 才使用这个 Serializer
        template.setDefaultSerializer(GENERIC_FAST_JSON_REDIS_SERIALIZER);
        // factory
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 RedisCacheManager，使用 cache 注解管理 redis 缓存
     */
    @Bean("Default")
    @Primary
//    @Override
    public CacheManager cacheManagerDefault(RedisConnectionFactory connectionFactory) {
        // 初始化一个 RedisCacheWriter
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        // 设置默认过期时间：30 min
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                 .disableCachingNullValues()
                // 使用注解时 key、value 的序列化方式
                .serializeKeysWith(STRING_PAIR);
//                .serializeValuesWith(GENERIC_FAST_JSON_PAIR);
        return new RedisCacheManager(cacheWriter, defaultCacheConfig);
    }

    @Bean("AttributeCache")
    public CacheManager AttributeCacheManager(RedisConnectionFactory connectionFactory) {
        // 初始化一个 RedisCacheWriter
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisSerializationContext.SerializationPair<DeviceAttribute> pair
                = RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer<>(DeviceAttribute.class));
        // 设置默认过期时间：30 min
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                // .disableCachingNullValues()
                // 使用注解时 key、value 的序列化方式
                .serializeKeysWith(STRING_PAIR)
                .serializeValuesWith(pair);
        return new RedisCacheManager(cacheWriter, defaultCacheConfig);
    }

    @Bean("DeviceTypeCache")
    public CacheManager deviceTypeCacheManager(RedisConnectionFactory connectionFactory) {
        // 初始化一个 RedisCacheWriter
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisSerializationContext.SerializationPair<DeviceType> pair
                = RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer<>(DeviceType.class));
        // 设置默认过期时间：30 min
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
//                 .disableCachingNullValues()
                // 使用注解时 key、value 的序列化方式
                .serializeKeysWith(STRING_PAIR);
//                .serializeValuesWith(pair);
        return new RedisCacheManager(cacheWriter, defaultCacheConfig);
    }

    @Bean("EquipmentCache")
    public CacheManager equipmentCacheManager(RedisConnectionFactory connectionFactory) {
        // 初始化一个 RedisCacheWriter
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisSerializationContext.SerializationPair<Equipment> pair
                = RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer<>(Equipment.class));
        // 设置默认过期时间：30 min
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
//                 .disableCachingNullValues()
                // 使用注解时 key、value 的序列化方式
                .serializeKeysWith(STRING_PAIR);
//                .serializeValuesWith(pair);
        return new RedisCacheManager(cacheWriter, defaultCacheConfig);
    }

    @Bean("StatusHistoryCache")
    public CacheManager statusHistoryCacheManager(RedisConnectionFactory connectionFactory) {
        // 初始化一个 RedisCacheWriter
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisSerializationContext.SerializationPair<StatusHistory> pair
                = RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer<>(StatusHistory.class));
        // 设置默认过期时间：30 min
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
//                 .disableCachingNullValues()
                // 使用注解时 key、value 的序列化方式
                .serializeKeysWith(STRING_PAIR)
                .serializeValuesWith(pair);
        return new RedisCacheManager(cacheWriter, defaultCacheConfig);
    }

    @Bean("AttributeDOCache")
    public CacheManager attributeDOCacheManager(RedisConnectionFactory connectionFactory) {
        // 初始化一个 RedisCacheWriter
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        RedisSerializationContext.SerializationPair<DeviceAttributeDO> pair
                = RedisSerializationContext.SerializationPair.fromSerializer(new FastJsonRedisSerializer<>(DeviceAttributeDO.class));
        // 设置默认过期时间：30 min
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
//                 .disableCachingNullValues()
                // 使用注解时 key、value 的序列化方式
                .serializeKeysWith(STRING_PAIR)
                .serializeValuesWith(pair);
        return new RedisCacheManager(cacheWriter, defaultCacheConfig);
    }
}