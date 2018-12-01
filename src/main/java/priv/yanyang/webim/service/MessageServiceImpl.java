package priv.yanyang.webim.service;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import priv.yanyang.webim.common.ConcurrentHashMap2;
import priv.yanyang.webim.common.KeyGenerate;
import priv.yanyang.webim.entity.Message;
import priv.yanyang.webim.service.cachad.MessageCached;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.TimeoutException;

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
     * 获取消息等待时间
     */
    public final static long WAIT_MSG_MSEC = 30 * 1000;

    /**
     *消息计数器锁过期时间
     */
    public final static long MESSAGE_COUNT_LOCK_EXPIRED = 5000L;

    /**
     *消息计数器锁轮询时间
     */
    public final static long MESSAGE_COUNT_POLL_SLEEP_TIME = 200;

    /**
     *消息计数器锁超时时间
     */
    public final static long MESSAGE_COUNT_LOCK_TIMEOUT = MESSAGE_COUNT_LOCK_EXPIRED;



    /**
     * 消息列表存活时间
     */
    public final static int MESSAGE_LIST_EXPIRED = 60 * 60;

    @Autowired
    private ClientService clientService;

    @Autowired
    private JedisPool jedisPool;
/*
    *//**
     * 查询消息列表
     * @param start 从这个条数开始获取
     * @param token
     * @param channel
     * @return
     *//*
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
    }*/

    /**
     * 查询消息列表
     * @param start 从这个条数开始获取
     * @param token
     * @param channel
     * @return
     */
    private List<String> getMessages(int start,String token,String channel){
        logger.debug("Method\tMessageServiceImpl.getMessages");
        List<String> msgs = new ArrayList<String>();

        int msgsIndex = start / MESSAGE_LIST_SIZE;//要读取的消息列表索引
        int currentStart = (start - 1) % MESSAGE_LIST_SIZE;//读取的位置


        String msgsIndexKey = MessageCached.messageIndexKey(token, channel, msgsIndex);
        Jedis jedis = jedisPool.getResource();
        List<String> channelMsgs = jedis.lrange(msgsIndexKey, currentStart, -1);
        jedis.close();

        int channelMsgsSize = channelMsgs.size();
        //读取到新的消息
        if(channelMsgs.size() > 0 ){
            msgs.addAll(channelMsgs);
/*
            //是否读取到了结尾
            if((MESSAGE_LIST_SIZE - currentStart - channelMsgsSize) == 0){
                //尝试读取下一个列表
                //do {
                    msgsIndexKey = MessageCached.messageIndexKey(token, channel, ++msgsIndex);
                    channelMsgs = jedis.lrange(msgsIndexKey, 0, -1);
                    channelMsgsSize = channelMsgs.size();
                    if (channelMsgsSize > 0)
                        msgs.addAll(channelMsgs);
               // }while (channelMsgsSize == MESSAGE_LIST_SIZE);
            }
*/
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

        final String  clientIndexKey = KeyGenerate.messageClientIndexKey(apiKey,channel,clientId);

        DeferredResult<List> deferredResult =  new DeferredResult<List>(WAIT_MSG_MSEC);
        final List msgs = new ArrayList();

        //当前已读的消息index
        Integer clientMsgIndex = clientService.getClientIndex(clientIndexKey);
        boolean isWait = true;

        //客户端是第一次连接，需要更新到现在消息的最新数目，
        if(null == clientMsgIndex){
            Jedis jedis = jedisPool.getResource();
            int msgCount = MessageCached.getMessageCount(apiKey,channel,jedis);
            jedis.close();
            clientService.updateClientIndex(clientIndexKey,msgCount);
            clientMsgIndex = msgCount;
        }else{//
            //客户端不是第一次连接，尝试获取最新的消息
            msgs.addAll(getMessages(clientMsgIndex + 1,apiKey,channel));
            int msgCount = msgs.size();
            isWait = msgCount == 0 ? true : false;
        }


        //等待新的消息
        if(isWait){
            final int waitMessageIndex = clientMsgIndex + 1;
            String MessageNotifyKey = MessageCached.generatePrefix(apiKey,channel);
            Observer obs  = new Observer(){
                @Override
                public void update(Observable o, Object msg) {
                    Long newMessageIndex = ((Message)msg).getIndex();

                    if(waitMessageIndex == newMessageIndex){
                        msgs.add(JSON.toJSONString(msg));
                    }else{
                        msgs.addAll(getMessages(waitMessageIndex,apiKey,channel));
                    }
                    if(deferredResult.setResult(msgs)){
                        clientService.updateClientIndex(clientIndexKey,waitMessageIndex +  msgs.size() - 1);
                    }
                }

            };

            deferredResult.onTimeout(()->{
                deferredResult.setResult(null);
                MessageNotifyMap.get(MessageNotifyKey).deleteObserver(obs);
            });

            MessageNotifyMap.getOrPut(MessageNotifyKey,new MessageNotify()).addObserver(obs);

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
    public  void add(Message message){
        logger.info("Method\tMessageServiceImpl.add");
        logger.info("message = [" + message + "]");

        String MessageNotifyKey = MessageCached.generatePrefix(message.getApiKey(),message.getChannel());

        Jedis jedis;
        //redis key 消息总数
        String msgCountKey = MessageCached.messageCountKey(message);
        //redis key 消息总数锁
        String msgCountKeyLock = "lock." + msgCountKey;
        //redis key 消息列表
        String msgsKey;
        //消息总数
        long msgCount;

        boolean unlockPoll = true;
        Date now = new Date();
        message.setCreateTime(now);
        long currentTime = now.getTime();

        do {
            logger.info(String.format("[Thread ID %d] open redis.",Thread.currentThread().getId()));
            jedis = jedisPool.getResource();
            logger.info(String.format("[Thread ID %d] open success",Thread.currentThread().getId()));
            //[[[[[[注意加锁]]]]]
            String isSuccess = jedis.set(msgCountKeyLock,"1","NX","PX"
                    ,MESSAGE_COUNT_LOCK_EXPIRED);

            if("OK".equals(isSuccess)){
                //消息总数
                msgCount = jedis.incr(msgCountKey);
                message.setIndex(msgCount);
                //保存消息 msgCount 作为后缀
                String msgjson = JSON.toJSONString(message);
                msgsKey = KeyGenerate.messagesKey(message,(msgCount - 1) / MESSAGE_LIST_SIZE);
                jedis.rpush(msgsKey,msgjson);
                //jedis

                //[[[[[[注意解锁]]]]]
                jedis.del(msgCountKeyLock);

                /**
                 * 如果是第一次就设置存活时间
                 */
                if(msgCount % MESSAGE_LIST_SIZE == 0){
                    jedis.expire(msgsKey,MESSAGE_LIST_EXPIRED);
                }
                jedis.close();

                unlockPoll = false;
            }else {
                jedis.close();
                logger.info(String.format("[Thread ID %d] wait unlock",Thread.currentThread().getId()));
                try {
                    Thread.currentThread().sleep(MESSAGE_COUNT_POLL_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }while (unlockPoll &&
        currentTime - System.currentTimeMillis() < MESSAGE_COUNT_LOCK_TIMEOUT );

        /**
         * 没有得到锁，代表超时了
         */
        if (unlockPoll){
            logger.warn(String.format("[Thread ID %d] wait unlock timeout",Thread.currentThread().getId()));
            new TimeoutException("锁等待超时!");
        }else{
            logger.info(String.format("[Thread ID %d] post msg success",Thread.currentThread().getId()));
            //发布新的消息
            MessageNotify obsser = MessageNotifyMap.getOrPut(MessageNotifyKey,new MessageNotify());
            obsser.notifyAll(message);
            MessageNotifyMap.remove(MessageNotifyKey);
        }

    }




}
