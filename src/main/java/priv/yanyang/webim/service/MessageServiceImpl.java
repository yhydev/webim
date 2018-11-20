package priv.yanyang.webim.service;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
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
 * <h1>消息服务</h1>
 * <p>Redis 进行消息存储</p>
 */
@Service
public class MessageServiceImpl implements MessageService {


    Logger logger = Logger.getLogger(MessageServiceImpl.class);


    ConcurrentHashMap2<String,MessageNotify> MessageNotifyMap = new ConcurrentHashMap2<String,MessageNotify>();

    /**
     * 一个消息列表的大小
     */
    public final static int MESSAGE_LIST_SIZE = 100;

    /**
     * 消息列表存活时间
     */
    public final static int MESSAGE_LIST_EXPIRED = 60 * 60;

    @Autowired
    private JedisPool jedisPool;


    /**
     * 查询消息列表
     * @param start 从这个条数开始获取
     * @param token
     * @param channel
     * @return
     */
    public List<String> getMessages(int start,String token,String channel){
        logger.debug("Method\tMessageServiceImpl.getMessages");
        List<String> msgs = new ArrayList<String>();
        Jedis jedis = jedisPool.getResource();
        //获取通道内消息条数
        String msgCountKey = MessageCached.messageCountKey(token,channel);
        String msgCountStr = jedis.get(msgCountKey);

        //false 则代表通道内没有一条消息
        if(msgCountStr != null && start > 0) {
            //TODO
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
        jedis.close();
        return msgs;
    }


    /**
     * 获取最新消息，如果没有最新消息则等待 <b>waitSecond<b/> 毫秒
     * @param apiKey 开发者key
     * @param channel 开发者自定义 通道
     * @param waitSecond 等待消息时间，超时则返回
     * @param clientId 客户端标识
     * @return
     */
    @Override
    public DeferredResult<List> get(final String apiKey,final String channel, long waitSecond, String clientId) {
        logger.debug("Method\tMessageServiceImpl.get");

        DeferredResult<List> deferredResult =  new DeferredResult<List>(waitSecond);
        List msgs = null;
        String MessageNotifyKey = MessageCached.generatePrefix(apiKey,channel);

        Jedis jedis = jedisPool.getResource();
        //当前已读的消息index
        int clientMsgIndex = MessageCached.getClientMessageIndex(apiKey,channel,clientId,jedis);
        boolean isWait = true;

        //客户端是第一次连接
        if(clientMsgIndex == 0){
            int msgCount = MessageCached.getMessageCount(apiKey,channel,jedis);
            //如果通道列表消息总数为0
            isWait = 0 == clientMsgIndex;

            if(!isWait){
                MessageCached.setClientMessageIndex(apiKey,channel,clientId,jedis,msgCount);
            }
            clientMsgIndex = msgCount;
        }else{// 客户端不是第一次连接
            //获取最新的消息
            msgs = getMessages(clientMsgIndex + 1,apiKey,channel);
            int msgCount = msgs.size();
            isWait = msgCount == 0 ? true : false;
            MessageCached.setClientMessageIndex(apiKey,channel,clientId,jedis,clientMsgIndex + msgCount);
        }

        //等待新的消息
        if(isWait){
            final int waitMessageIndex = clientMsgIndex + 1;
            Observer obs  = new Observer(){
                @Override
                public void update(Observable o, Object msg) {
                    int newMessageIndex = ((Message)msg).getIndex();
                    List<String> msgData = null;

                    if(waitMessageIndex == newMessageIndex){
                        msgData = new ArrayList();
                        msgData.add(JSON.toJSONString(msg));
                        Jedis jedis1 = jedisPool.getResource();
                        MessageCached.setClientMessageIndex(apiKey,channel,clientId,jedis1,newMessageIndex);
                        jedis1.close();
                    }

                    deferredResult.setResult(msgData);
                }

            };

            deferredResult.onTimeout(()->{
                deferredResult.setResult(null);
                MessageNotifyMap.get(MessageNotifyKey).deleteObserver(obs);
            });

            MessageNotifyMap.getOrPut(MessageNotifyKey,new MessageNotify()).addObserver(obs);

        }else{//这个是有未读的消息，所以就不用等新的消息啦
            deferredResult.setResult(msgs);
        }
        jedis.close();
        return deferredResult;
    }

    /**
     * 发送一条新的消息，先存到缓存中，然后redis 发布
     * @param message
     * @return
     */
    public  Message add(Message message){
        logger.debug("Method\tMessageServiceImpl.add");

        String MessageNotifyKey = MessageCached.generatePrefix(message.getApiKey(),message.getChannel());

        Jedis pool = jedisPool.getResource();

        //获取当前通道(message.apiKey 和 message.token) 的消息总数key
        String msgCountKey = MessageCached.messageCountKey(message);
        Integer msgCount = 0;
        String msgsKey = null;
        Integer msgsCount = null;

        synchronized (this){
            //消息总数
            String msgCountStr = pool.get(msgCountKey);
            msgCount = null == msgCountStr ? 1 : Integer.valueOf(msgCountStr) + 1;

            message.setIndex(msgCount);
            //保存消息 msgCount 作为后缀
            String msgjson = JSON.toJSONString(message);
            msgsKey = MessageCached.messageIndexKey(message,(msgCount - 1) / MESSAGE_LIST_SIZE);
            pool.rpush(msgsKey,msgjson);

            //更新当前通道的消息总数
            pool.set(msgCountKey,msgCount.toString());
        }

        /**
         * 如果是第一次就设置存活时间
         */
        if(msgCount % MESSAGE_LIST_SIZE == 0){
            pool.expire(msgsKey,MESSAGE_LIST_EXPIRED);
        }

        pool.close();

        //发布新的消息
        MessageNotify obsser = MessageNotifyMap.getOrPut(MessageNotifyKey,new MessageNotify());
        obsser.notifyAll(message);
        MessageNotifyMap.remove(MessageNotifyKey);
        return message;
    }

}
