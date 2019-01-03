package priv.yanyang.webim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.yanyang.common.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisService redisService;

    @Override
    public ResponseBody<Long> getChannelClients(String uniqueChannel) {
        Jedis jedis = jedisPool.getResource();
        //jedis.get


        return null;
    }
}
