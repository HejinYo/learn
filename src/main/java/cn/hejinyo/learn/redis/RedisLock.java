package cn.hejinyo.learn.redis;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/11/22 21:49
 * @apiNote :
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的分布式锁实现
 */
@Service
public class RedisLock {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLock.class);
    //加锁超时时间，单位毫秒， 即：加锁时间内执行完操作，如果未完成会有并发现象
    @Autowired
    private StringRedisTemplate redisTemplate;

   /* public RedisLock(StringRedisTemplate redisTemplate, long timeout) {
        this.redisTemplate = redisTemplate;
        this.lockTimeout = timeout;
    }*/

    /**
     * 加锁
     * 取到锁加锁，取不到锁就返回
     *
     * @param lockKey
     * @param threadName
     * @return
     */

    public Long lock(String lockKey, String threadName) {
        long lockTimeout = 5000;
        LOG.debug(threadName + "开始执行加锁");
        //锁时间
        Long lock_timeout = currtTimeForRedis() + lockTimeout + 1;
        if (redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //定义序列化方式
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value = serializer.serialize(lock_timeout.toString());
                return redisConnection.setNX(lockKey.getBytes(), value);
            }
        })) {
            //如果加锁成功
            LOG.debug(threadName + "加锁成功+1");
            //设置超时时间，释放内存
            redisTemplate.expire(lockKey, lockTimeout, TimeUnit.MILLISECONDS);
            return lock_timeout;
        } else {
            //获取redis里面的时间
            String result = redisTemplate.opsForValue().get(lockKey);
            Long currt_lock_timeout_str = result == null ? null : Long.parseLong(result);
            //锁已经失效
            if (currt_lock_timeout_str != null && currt_lock_timeout_str < System.currentTimeMillis()) {
                //判断是否为空，不为空时，说明已经失效，如果被其他线程设置了值，则第二个条件判断无法执行
                //获取上一个锁到期时间，并设置现在的锁到期时间
                Long old_lock_timeout_Str = Long.valueOf(redisTemplate.opsForValue().getAndSet(lockKey, lock_timeout.toString()));
                if (old_lock_timeout_Str != null && old_lock_timeout_Str.equals(currt_lock_timeout_str)) {
                    //多线程运行时，多个线程签好都到了这里，但只有一个线程的设置值和当前值相同，它才有权利获取锁
                    LOG.debug(threadName + "加锁成功+2");
                    //设置超时间，释放内存
                    redisTemplate.expire(lockKey, lockTimeout, TimeUnit.MILLISECONDS);
                    //返回加锁时间
                    return lock_timeout;
                }
            }
        }
        return null;
    }

    /**
     * 解锁
     *
     * @param lockKey
     * @param lockValue
     * @param threadName
     */

    public void unlock(String lockKey, long lockValue, String threadName) {
        LOG.debug(threadName + "执行解锁==========");//正常直接删除 如果异常关闭判断加锁会判断过期时间
        //获取redis中设置的时间
        String result = redisTemplate.opsForValue().get(lockKey);
        Long currt_lock_timeout_str = result == null ? null : Long.valueOf(result);
        //如果是加锁者，则删除锁， 如果不是，则等待自动过期，重新竞争加锁
        if (currt_lock_timeout_str != null && currt_lock_timeout_str == lockValue) {
            redisTemplate.delete(lockKey);
            LOG.debug(threadName + "解锁成功------------------");
        }
    }

    /**
     * 多服务器集群，使用下面的方法，代替System.currentTimeMillis()，获取redis时间，避免多服务的时间不一致问题！！！
     *
     * @return
     */

    public long currtTimeForRedis() {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.time();
            }
        });
    }
}
