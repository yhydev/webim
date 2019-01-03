package priv.yanyang.webim.service;

public interface RedisService {

    Long getLong(String key);

    Long inc(String key);

    Long dec(String key);



}
