package priv.yanyang.webim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import priv.yanyang.webim.entity.Message;
import priv.yanyang.webim.service.MessageService;
import java.util.List;


@RequestMapping("/message")
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public Object post(Message message,String apiKey){
        messageService.add(apiKey,message);
        return null;
    }

    @GetMapping
    public DeferredResult<List> get(String channel,String clientId,String apiKey){
        return messageService.get(apiKey,channel,clientId);
    }

}
