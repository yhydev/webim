package priv.yanyang.webim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private JedisPool jedisPool;

    @Override
    public Long getLong(String key) {
        return null;
    }

    @Override
    public Long inc(String key) {
        return null;
    }

    @Override
    public Long dec(String key) {
        return null;
    }
}
