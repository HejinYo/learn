package cn.hejinyo.learn.redis;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-07-17 21:12
 */
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ValueOperations<String, Object> valueOperations;
    @Autowired
    private HashOperations<String, String, Object> hashOperations;
    @Autowired
    private ListOperations<String, Object> listOperations;
    @Autowired
    private SetOperations<String, Object> setOperations;
    @Autowired
    private ZSetOperations<String, Object> zSetOperations;
    /**
     * 默认过期时长，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;
    private final static Gson gson = new Gson();

    public void set(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = (String) valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = (String) valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * @param key
     * @param value
     * @param expireAt 失效日期
     */
    public void set(String key, Object value, Date expireAt) {
        valueOperations.set(key, toJson(value));
        if (expireAt != null) {
            redisTemplate.expireAt(key, expireAt);
        }
    }

    /**
     * 累加
     *
     * @param key
     * @param delta
     * @param expireAt
     * @return
     */
    public Long increment(String key, Long delta, Date expireAt) {
        Long resule = valueOperations.increment(key, delta);
        if (expireAt != null) {
            redisTemplate.expireAt(key, expireAt);
        }
        return resule;
    }

    /**
     * 压栈
     *
     * @param key
     * @param value
     */
    public void leftPush(String key, Object value) {
        listOperations.leftPush(key, toJson(value));
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T leftPop(String key, Class<T> clazz) {
        Object value = listOperations.leftPop(key);
        return value == null ? null : fromJson(String.valueOf(value), clazz);
    }


    /**
     * 入队
     *
     * @param key
     * @param value
     * @return
     */
    public Long rightPush(String key, Object value) {
        return listOperations.rightPush(key, toJson(value));
    }

    /**
     * 移除并获取列表最后一个元素
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T rightPop(String key, Class<T> clazz) {
        Object value = listOperations.rightPop(key);
        return value == null ? null : fromJson(String.valueOf(value), clazz);
    }


    /**
     * 栈/队列长
     *
     * @param key
     * @return
     */
    public Long listSize(String key) {
        return listOperations.size(key);
    }
}
