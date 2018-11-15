package priv.yanyang.webim.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MessageServiceImplTest {

    @Autowired
    private MessageServiceImpl messageServiceImpl;

    @Autowired
    private JedisPool jedisPool;

    @Test
    public void getMessages() {
        Jedis jedis = jedisPool.getResource();
        List msg = messageServiceImpl.getMessages(3,"xxx","yyy",jedis);
        System.out.println("msg.size() = " + msg.size());
    }
}