package cn.hejinyo.learn.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.concurrent.*;

/**
 * ActiveMQ线程池工具类,简单单例模式修改为双检锁方式
 *
 * @author : HejinYo   hejinyo@gmail.com     2017/11/9 22:02
 * @apiNote :
 */
public class ThreadPoolUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);
    private static final String PROFILE_NAME = "application.yml";
    private final static String PROFILE_COREPOOLSIZE = "corePoolSize";
    private final static String PROFILE_MAXIMUMPOOLSIZE = "maximumPoolSize";
    private final static String PROFILE_KEEPACTIVETIME = "keepActiveTime";
    private final static String COREPOOLSIZE_DEFAULT = "4";
    private final static String MAXIMUMPOOLSIZE_DEFAULT = "4";
    private final static String KEEPACTIVETIME_DEFAULT = "200";
    private volatile static ThreadPoolUtil instance;
    private static ExecutorService singleThreadPool;

    /**
     * 核心池大小
     */
    private static int corePoolSize;
    /**
     * 线程池最大能接受多少线程
     */
    private static int maximumPoolSize;
    /**
     * 当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期
     */
    private static long keepActiveTime;

    public static synchronized ThreadPoolUtil getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolUtil.class) {
                if (instance == null) {
                    //创建工具类实例
                    instance = new ThreadPoolUtil();
                    corePoolSize = Integer.valueOf(getValue(PROFILE_COREPOOLSIZE, COREPOOLSIZE_DEFAULT));
                    maximumPoolSize = Integer.valueOf(getValue(PROFILE_MAXIMUMPOOLSIZE, MAXIMUMPOOLSIZE_DEFAULT));
                    keepActiveTime = Long.valueOf(getValue(PROFILE_KEEPACTIVETIME, KEEPACTIVETIME_DEFAULT));
                }
            }
        }
        return instance;
    }

    public void execute(Runnable comm) {
        //创建线程池对象
        if (singleThreadPool == null) {
            //设置时间单位，秒
            TimeUnit timeUnit = TimeUnit.SECONDS;
            singleThreadPool = getExecutorService(corePoolSize, maximumPoolSize, keepActiveTime, timeUnit);
            logger.debug("线程池创建成功[核心池大小:" + corePoolSize + ";最大线程：" + maximumPoolSize + ";生命周期:" + keepActiveTime + "]");
        }
        singleThreadPool.execute(comm);
    }

    /**
     * 创建线程池对象
     */
    private static ExecutorService getExecutorService(int corePoolSize, int maximumPoolSize, long keepActiveTime, TimeUnit timeUnit) {
        //ThreadFactoryBuilder().setNameFormat("activeMQ-pool-%d")
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().build();
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepActiveTime, timeUnit,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 从主配置文件中获取value
     */
    private static String getValue(String key, String defaultValue) {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(PROFILE_NAME));
        return yaml.getObject().getProperty(key, defaultValue);
    }
}
