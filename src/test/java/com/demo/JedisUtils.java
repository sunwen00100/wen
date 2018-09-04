package com.demo;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@SpringBootTest
public class JedisUtils {
    private static JedisPool jedisPool = null;
    private static Jedis jedis;

    static {
        jedis = getJedisPool().getResource();
    }

    /**
     * 构建redis连接池
     */
    public static JedisPool getJedisPool() {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(1024); // 可用连接实例的最大数目,如果赋值为-1,表示不限制.
            config.setMaxIdle(5); // 控制一个Pool最多有多少个状态为idle(空闲的)jedis实例,默认值也是8
            config.setMaxWaitMillis(1000 * 100); // 等待可用连接的最大时间,单位毫秒,默认值为-1,表示永不超时/如果超过等待时间,则直接抛出异常
            config.setTestOnBorrow(true); // 在borrow一个jedis实例时,是否提前进行validate操作,如果为true,则得到的jedis实例均是可用的
            jedisPool = new JedisPool(config, "127.0.0.1", 6379);
        }
        return jedisPool;
    }

    /**
     * 释放jedis资源
     */
    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    @Test
    public void testString() {
        jedis.set("name", "webb"); // 添加数据
        System.out.println("name -> " + jedis.get("name"));

        jedis.append("name", " , javaer"); // 拼接
        System.out.println("name -> " + jedis.get("name"));

        jedis.del("name"); // 删除数据
        System.out.println("name -> " + jedis.get("name"));

        jedis.mset("name", "webb", "age", "24"); // 设置多个键值对
        jedis.incr("age"); // 进行加1操作

        System.out.println("name -> " + jedis.get("name") + ", age -> " + jedis.get("age"));
    }

    @Test
    public void testList() {
        String key = "java framework";

        jedis.lpush(key, "spring");
        jedis.lpush(key, "spring mvc");
        jedis.lpush(key, "mybatis");

        System.out.println(jedis.lrange(key, 0 , -1)); // -1表示取得所有

        jedis.del(key);
        jedis.rpush(key, "spring");
        jedis.rpush(key, "spring mvc");
        jedis.rpush(key, "mybatis");

        System.out.println(jedis.lrange(key, 0 , -1)); // -1表示取得所有

        System.out.println(jedis.llen(key)); // 列表长度
        System.out.println(jedis.lrange(key, 0, 3));
        jedis.lset(key, 0 , "redis"); // 修改列表中单个值
        System.out.println(jedis.lindex(key, 1)); // 获取列表指定下标的值
        System.out.println(jedis.lpop(key)); // 列表出栈
        System.out.println(jedis.lrange(key, 0 , -1)); // -1表示取得所有
    }
}
