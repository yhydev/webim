package priv.yanyang.webim.service;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import priv.yanyang.webim.common.KeyGenerate;
import priv.yanyang.webim.entity.Message;
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

    @Autowired
    private ObservableService observableService;

    /**
     * 一个消息列表的大小
     */
    public final static int MESSAGE_LIST_SIZE = 100;

    /**
     * 获取消息等待时间
     */
    public final static long WAIT_MSG_MSEC = 30 * 1000;

    /**
     * Redis 消息列表存活时间
     */
    public final static int MESSAGE_LIST_EXPIRED = 60 * 60;

/*
    */
/**
     * 当 <b>public DeferredResult<List> get(final String,final String, String )<b/> 被执行，会检查客户端是否在此。
     * 如果存在，则表示上次的断开是客户端主动断开。
     * 不存在则表示这是第一次连接，或上次是服务端返回数据后断开。
     *
     *//*

    private HashMap<String,Observer> clients = new HashMap<String,Observer>();
*/

    @Autowired
    private ClientService clientService;

    @Autowired
    private JedisPool jedisPool;


    private int getMessageCount(String uniqueChannel){
        Jedis jedis = jedisPool.getResource();
        String countStr = jedis.get(uniqueChannel);
        jedis.close();
        return countStr == null ? 0 : Integer.valueOf(countStr);
    }


    /**
     * 获取消息列表
     * @param uniqueChannel
     * @param start
     * @return
     */
    private List<String> getMessages(String uniqueChannel,int start){
        logger.info("Method\tMessageServiceImpl.getMessages");
        List<String> msgs = new ArrayList<String>();

        int msgsIndex = start / MESSAGE_LIST_SIZE;//要读取的消息列表索引
        int currentStart = (start - 1) % MESSAGE_LIST_SIZE;//读取的位置
        String msgsIndexKey = KeyGenerate.messagesIndexKey(uniqueChannel, msgsIndex);

        Jedis jedis = jedisPool.getResource();
        List<String> channelMsgs = jedis.lrange(msgsIndexKey, currentStart, -1);
        jedis.close();

        //读取到新的消息
        if(channelMsgs.size() > 0 ){
            msgs.addAll(channelMsgs);
        }

        return msgs;
    }






    /**
     * 获取最新消息，如果没有最新消息则等待 <b>waitSecond<b/> 毫秒
     * @param apiKey 开发者key
     * @param channel 开发者自定义 通道
     * @param clientId 客户端标识
     * @return
     */
    @Override
    public DeferredResult<List> get(final String apiKey,final String channel, String clientId) {
        logger.info("Method\tMessageServiceImpl.get");
        logger.info("apiKey = [" + apiKey + "], channel = [" + channel + "], clientId = [" + clientId + "]");

        final String uniqueChannel = KeyGenerate.generatePrefix(apiKey,channel);
        final String  clientIndexKey = KeyGenerate.messageClientIndexKey(uniqueChannel,clientId);

        DeferredResult<List> deferredResult =  new DeferredResult<List>(WAIT_MSG_MSEC);
        final List msgs = new ArrayList();

        //当前已读的消息index
        Integer clientMsgIndex = clientService.getClientIndex(clientIndexKey);
        boolean isWait = true;

        //客户端是第一次连接，需要更新到现在消息的最新数目，
        if(null == clientMsgIndex){
            Jedis jedis = jedisPool.getResource();
            int msgCount = getMessageCount(KeyGenerate.messageCountKey(uniqueChannel));
            jedis.close();
            clientService.updateClientIndex(clientIndexKey,msgCount);
            clientMsgIndex = msgCount;
        }else{//
            //客户端不是第一次连接，尝试获取最新的消息
            msgs.addAll(getMessages(uniqueChannel,clientMsgIndex + 1));
            int msgCount = msgs.size();
            isWait = msgCount == 0 ? true : false;
        }

        //等待新的消息
        if(isWait){
            final int waitMessageIndex = clientMsgIndex + 1;
            Observer obs  = new Observer(){
                @Override
                public void update(Observable o, Object msg) {
                    Long newMessageIndex = ((Message)msg).getIndex();

                    if(waitMessageIndex == newMessageIndex){
                        msgs.add(JSON.toJSONString(msg));
                    }else{
                        msgs.addAll(getMessages(uniqueChannel,waitMessageIndex));
                    }
                    if(deferredResult.setResult(msgs)){
                        clientService.updateClientIndex(clientIndexKey,waitMessageIndex +  msgs.size() - 1);
                    }
                }

            };

            /*deferredResult.onError((throwable)->{
                logger.warn(throwable);
            });
*/
            deferredResult.onTimeout(()->{
                deferredResult.setResult(null);
                observableService.deleteObserver(uniqueChannel,obs);
            });
            observableService.addObserver(uniqueChannel,obs);

        }else{//这个是有未读的消息，所以就不用等新的消息啦
            if(deferredResult.setResult(msgs) && msgs.size() > 0 ){
                clientService.updateClientIndex(clientIndexKey,clientMsgIndex +  msgs.size());
            }
        }
        return deferredResult;
    }


    /**
     * 发送一条新的消息，先存到缓存中，然后redis 发布
     * @param message
     * @return
     */
    public  void add(String apiKey,Message message){
        logger.info("Method\tMessageServiceImpl.add");
        logger.info("message = [" + message + "]");
        String uniqueChannel = KeyGenerate.generatePrefix(apiKey,message.getChannel());
        //redis key 消息总数
        String msgCountKey = KeyGenerate.messageCountKey(uniqueChannel);
        String msgsKey;
        //消息总数
        long msgCount;
        Date now = new Date();
        message.setCreateTime(now);
        Jedis jedis = jedisPool.getResource();
        msgCount = jedis.incr(msgCountKey);
        message.setIndex(msgCount);
        //保存消息 msgCount 作为后缀
        String msgjson = JSON.toJSONString(message);
        msgsKey = KeyGenerate.messagesKey(uniqueChannel,(msgCount - 1) / MESSAGE_LIST_SIZE);
        jedis.rpush(msgsKey,msgjson);
        /**
         * message list最后一条设置过期时间
         */
        if(msgCount % MESSAGE_LIST_SIZE == 0){
            jedis.expire(msgsKey,MESSAGE_LIST_EXPIRED);
        }
        jedis.close();
        logger.info(String.format("[Thread ID %d] post msg success",Thread.currentThread().getId()));
        observableService.removeThenNotifyObservable(uniqueChannel,message);
    }

}
