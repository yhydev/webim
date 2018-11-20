package priv.yanyang.webim;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import priv.yanyang.webim.config.JedisConfig;
import priv.yanyang.webim.config.JedisPoolConfig;
import priv.yanyang.webim.secret.Authentication;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class Application {

    static Logger logger = Logger.getLogger(Application.class);
    
    static {


        try {
            InputStream stream = Application.class.getResourceAsStream("/log4j.properties");
            //初始化日志配置
            PropertyConfigurator.configure(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class,args);
    }


    /**
     * 认证 FilterRegister
     * @param authentication
     * @return
     */
    @Bean
    public FilterRegistrationBean AuthFilterRegisterBean(@Autowired Authentication authentication){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.addUrlPatterns("/message");
        filterRegistrationBean.setFilter(authentication);
        return filterRegistrationBean;
    }



    @Bean
    public JedisPool jedisPool(@Autowired JedisPoolConfig jedisPoolConfig, @Autowired JedisConfig jedisConfig) throws InterruptedException {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,jedisConfig.getIp(),jedisConfig.getPort());
        return jedisPool;
    }



}
