package priv.yanyang.webim.service.cachad;

import priv.yanyang.webim.entity.Message;
import redis.clients.jedis.Jedis;

public class MessageCached {

    public static String generatePrefix(Message msg){
        return generatePrefix(msg.getApiKey(),msg.getChannel());
    }

    public static String generatePrefix(String token,String channel){
        return String.format("apiKey_%s_channel_%s",token,channel);
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

        String uniqueChannel = messageCountKey(token,channel);

        String msgCountStr = jedis.get(uniqueChannel);
        int msgCount = 0;
        if(msgCountStr == null){
            jedis.set(uniqueChannel,"0");
        }else{
            msgCount = Integer.valueOf(msgCountStr);
        }
        return msgCount;
    }

}
