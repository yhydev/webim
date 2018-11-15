package priv.yanyang.webim.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import priv.yanyang.webim.config.JedisConfig;

@Component
public class JedisClient {

    @Autowired
    private JedisConfig jedisConfig;


    public JedisClient(){

    }


}
