package priv.yanyang.webim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <p>客户端服务实现。<p/>
 */
@Service
public class ClientServiceImpl implements ClientService {

    /**
     * <p>客户端过期时间，每次更新客户端则重置为此时间。</p>
     */
    public final static int CLIENT_EXPIRED = 60 * 30;


    @Autowired
    JedisPool jedisPool;

    /**
     * <p>更新客户端，没有则新增一个</p>
     * @param clientIdIndexKey <p>由priv.yanyang.webim.common.messageClientIndexKey 生成<p/>
     */
    @Override
    public void updateClientIndex(String clientIdIndexKey,long index){
        Jedis jedis = jedisPool.getResource();
        jedis.setex(clientIdIndexKey,CLIENT_EXPIRED,String.valueOf(index));
        jedis.close();
    }

    /**
     *<p>获取客户端索引(索引即客户端已读消息的索引)</p>
     * @param clientIdIndexKey
     * @return null 为第一次连接
     */
    @Override
    public Integer getClientIndex(String clientIdIndexKey) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.get(clientIdIndexKey);
        jedis.close();
        return index == null ? null : Integer.valueOf(index);
    }


}
