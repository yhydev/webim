package priv.yanyang.webim.common;

import priv.yanyang.webim.entity.Message;

public interface KeyGenerate {

     static String generatePrefix(Message msg){
        return generatePrefix(msg.getApiKey(),msg.getChannel());
    }

     static String generatePrefix(String apiKey,String channel){
        return String.format("apiKey_%s_channel_%s",apiKey,channel);
    }


     static String messageCountKey(Message message){
        return String.format("%s_messageCount",generatePrefix(message));
    }

     static String messageCountKey(String apiKey,String channel){
        return String.format("%s_messageCount",generatePrefix(apiKey,channel));
    }

     static String messagesKey(Message message,Long index){
        return messagesKey(message.getApiKey(),message.getChannel(),index);
    }

     static String messagesKey(String apiKey,String channel,long index){
        return String.format("%s_index_%s",generatePrefix(apiKey,channel),index);
    }

     static String messageClientIndexKey(String apiKey,String channel,String clientId){
        return String.format("%s_clientId_%s_index",generatePrefix(apiKey,channel),clientId);
    }



}
