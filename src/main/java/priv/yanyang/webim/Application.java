package priv.yanyang.webim;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import priv.yanyang.webim.config.JedisConfig;
import priv.yanyang.webim.config.JedisPoolConfig;
import priv.yanyang.webim.secret.Authentication;
import priv.yanyang.webim.service.MessageServiceImpl;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
@Import({MessageServiceImpl.class})
public class Application {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class,args);
    }

    @Bean
    public JedisPool jedisPool(@Autowired JedisPoolConfig jedisPoolConfig, @Autowired JedisConfig jedisConfig) throws InterruptedException {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,jedisConfig.getIp(),jedisConfig.getPort());
        return jedisPool;
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


}
