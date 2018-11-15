package priv.yanyang.webim.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jedis-pool-config")
public class JedisPoolConfig extends redis.clients.jedis.JedisPoolConfig {

}
