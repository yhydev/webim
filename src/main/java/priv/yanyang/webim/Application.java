package priv.yanyang.webim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import priv.yanyang.webim.config.JedisConfig;
import priv.yanyang.webim.config.JedisPoolConfig;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

@SpringBootApplication
public class Application {


    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class,args);
    }




    @Bean
    public JedisPool jedisPool(@Autowired JedisPoolConfig jedisPoolConfig, @Autowired JedisConfig jedisConfig) throws InterruptedException {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,jedisConfig.getIp(),jedisConfig.getPort());

        /*for (int i = 0; i < 9; i++) {
            jedisPool.getResource();
            System.out.println("getResource " + (i+1));
        }
        System.out.println("sleep..");
        Thread.sleep(Long.MAX_VALUE);
        System.out.println("end sleep..");*/
        return jedisPool;
    }



}
