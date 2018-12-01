package priv.yanyang.webim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.yanyang.common.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public interface ClientService {


    void updateClientIndex(String clientIdIndexKey,long index);

    Integer getClientIndex(String clientIdIndexKey);



}
