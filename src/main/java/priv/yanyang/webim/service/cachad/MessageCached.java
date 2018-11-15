package priv.yanyang.webim.service.cachad;

import priv.yanyang.webim.entity.Message;
import redis.clients.jedis.Jedis;

public class MessageCached {

    public static String generatePrefix(Message msg){
        return generatePrefix(msg.getApiKey(),msg.getChannel());
    }

    public static String generatePrefix(String token,String channel){
        return String.format("token_%s_channel_%s",token,channel);
    }


    public static String messageCountKey(Message message){
        return String.format("%s_messageCount",generatePrefix(message));
    }

    public static String messageCountKey(String token,String channel){
        return String.format("%s_messageCount",generatePrefix(token,channel));
    }

    public static String messageIndexKey(Message message,int index){
        return messageIndexKey(message.getApiKey(),message.getChannel(),index);
    }

    public static String messageIndexKey(String token,String channel,int index){
        return String.format("%s_index_%s",generatePrefix(token,channel),index);
    }

    public static String messageClientIndex(String token,String channel,String clientId){
        return String.format("%s_clientId_%s_index",generatePrefix(token,channel),clientId);
    }


    public static int getMessageCount(String token, String channel,Jedis jedis) {
        String msgCountStr = jedis.get(messageCountKey(token,channel));
        int msgCount = 0;
        if(msgCountStr == null){
            jedis.set(messageCountKey(token,channel),"0");
        }else{
            msgCount = Integer.valueOf(msgCountStr);
        }
        return msgCount;
    }

    public static int getClientMessageIndex(String token,String channel,String clientId,Jedis jedis){
        String clientMsgIndexKey = messageClientIndex(token,channel,clientId);
        String clientMsgIndexStr = jedis.get(clientMsgIndexKey);

        int clientIndex = 0;
        //true 客户端则是第一次连接
        if(clientMsgIndexStr != null) {
            clientIndex = Integer.valueOf(clientMsgIndexStr);
        }
        return clientIndex;
    }

    public static void setClientMessageIndex(String token,String channel,String clientId,Jedis jedis,int index){
        String key = messageClientIndex(token,channel,clientId);
        jedis.set(key,String.valueOf(index));
    }

}
