package priv.yanyang.webim.service;

import org.springframework.web.context.request.async.DeferredResult;
import priv.yanyang.webim.entity.Message;

import java.nio.channels.Channel;
import java.util.List;

/**
 * 消息服务
 */
public interface MessageService {

    DeferredResult<List> get(String token, String channel, String clientId);

    void add(String apiKey,Message message);



}
