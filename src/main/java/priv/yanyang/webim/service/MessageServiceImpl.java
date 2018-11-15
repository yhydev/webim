package priv.yanyang.webim.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import priv.yanyang.webim.common.ConcurrentHashMap2;
import priv.yanyang.webim.entity.Message;
import priv.yanyang.webim.service.cachad.MessageCached;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * 消息服务
 */
@Service
public class MessageServiceImpl implements MessageService {

    ConcurrentHashMap2<String,MessageNotify> MessageNotifyMap = new ConcurrentHashMap2<String,MessageNotify>();
    public static int MESSAGE_LIST_SIZE = 10;

    @Autowired
    private JedisPool jedisPool;


    /**
     * 查询消息列表
     * @param start 从这个条数开始获取
     * @param token
     * @param channel
     * @return
     */
    public List getMessages(int start,String token,String channel,Jedis jedis){
        List msgs = new ArrayList<Message>();

        //获取通道内消息条数
        String msgCountKey = MessageCached.messageCountKey(token,channel);
        String msgCountStr = jedis.get(msgCountKey);

        //false 则代表通道内没有一条消息
        if(msgCountStr != null && start > 0) {
            int msgCount = Integer.valueOf(msgCountStr);

            if (start < msgCount) {
                //已读消息列表索引
                int msgsIndex = (start - 1) / MESSAGE_LIST_SIZE;
                //最新消息列表索引
                int msgsLastIndex = (msgCount - 1) / MESSAGE_LIST_SIZE;

                //未读消息索引
                int notReadMsgIndex = (start % MESSAGE_LIST_SIZE) - 1;

                if (msgsIndex == msgsLastIndex) {
                    String msgsIndexKey = MessageCached.messageIndexKey(token, channel, msgsIndex );
                    msgs.addAll(jedis.lrange(msgsIndexKey, notReadMsgIndex, -1));
                } else {
                    String msgsIndexKey = MessageCached.messageIndexKey(token, channel, msgsIndex );
                    msgs.addAll(jedis.lrange(msgsIndexKey, notReadMsgIndex, -1));

                    msgsIndex++;
                    for (;msgsIndex <=msgsLastIndex; msgsIndex++) {
                        msgsIndexKey = MessageCached.messageIndexKey(token, channel, msgsIndex);
                        msgs.addAll(jedis.lrange(msgsIndexKey, 0, -1));
                    }

                }
            }
        }

        return msgs;
    }


    /**
     * 获取最新消息，如果没有最新消息则等待 <b>waitSecond<b/> 毫秒
     * @param openKey 开发者key
     * @param channel 开发者自定义 通道
     * @param waitSecond 等待消息时间，超时则返回
     * @param clientId 客户端标识
     * @return
     */
    @Override
    public DeferredResult<List> get(String openKey, String channel, long waitSecond, String clientId) {

        DeferredResult<List> deferredResult =  new DeferredResult<List>(waitSecond);
        List msgs = null;
        String MessageNotifyKey = MessageCached.generatePrefix(openKey,channel);

        Jedis jedis = jedisPool.getResource();
        //当前已读的消息index
        int clientMsgIndex = MessageCached.getClientMessageIndex(openKey,channel,clientId,jedis);
        //等待消息的index
        boolean isWait = false;

        //客户端是第一次连接
        if(clientMsgIndex == 0){
            MessageCached.setClientMessageIndex(openKey,channel,clientId,jedis,0);
        }

        if(clientMsgIndex > 0){
            msgs = getMessages(clientMsgIndex + 1,openKey,channel,jedis);
            //true 是没有新消息，所有下面要等待新的消息的到来
            isWait = msgs.size() == 0 ? true : false;
        } else{
            isWait = true;
        }

        //等待新的消息
        if(isWait){

            final Observer obs  = new Observer(){
                @Override
                public void update(Observable o, Object msg) {
                    List msgData = new ArrayList();
                    msgData.add(JSON.toJSONString(msg));
                    deferredResult.setResult(msgData);
                    Jedis jedis1 = jedisPool.getResource();
                    MessageCached.setClientMessageIndex(openKey,channel,clientId,jedis1,((Message)msg).getIndex());
                    jedis1.close();
                }

            };

            deferredResult.onTimeout(()->{
                deferredResult.setResult(null);
                MessageNotifyMap.get(MessageNotifyKey).deleteObserver(obs);
            });

            MessageNotifyMap.getOrPut(MessageNotifyKey,new MessageNotify()).addObserver(obs);

        }else{//这个是有未读的消息，所以就不用等新的消息啦
            deferredResult.setResult(msgs);
            MessageCached.setClientMessageIndex(openKey,channel,clientId,jedis,clientMsgIndex + msgs.size());
        }
        jedis.close();
        return deferredResult;
    }

    /**
     * 发送一条新的消息，先存到缓存中，然后redis 发布
     * @param message
     * @return
     */
    public Message add(Message message){
        String MessageNotifyKey = MessageCached.generatePrefix(message.getApiKey(),message.getChannel());

        Jedis pool = jedisPool.getResource();


        //获取当前通道(message.channel 和 message.token) 的消息总数
        String msgCountKey = MessageCached.messageCountKey(message);

        //消息总数
        String msgCountStr = pool.get(msgCountKey);
        Integer msgCount = 0;
        //true 表示有消息
        if(msgCountStr != null){
            msgCount = Integer.valueOf(msgCountStr);
        }
        msgCount++;
        message.setIndex(msgCount);

        //保存消息 msgCount 作为后缀
        String msgjson = JSON.toJSONString(message);
        pool.rpush(MessageCached.messageIndexKey(message,(msgCount - 1) / MESSAGE_LIST_SIZE),msgjson);

        //更新当前通道的消息总数
        pool.set(msgCountKey,msgCount.toString());

        //发布新的消息
        MessageNotify obsser = MessageNotifyMap.getOrPut(MessageNotifyKey,new MessageNotify());
        obsser.notifyAll(message);
        MessageNotifyMap.remove(MessageNotifyKey);
        pool.close();
        return message;
    }

}
