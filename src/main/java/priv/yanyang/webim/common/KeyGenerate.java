package priv.yanyang.webim.common;

import priv.yanyang.webim.entity.Message;

public interface KeyGenerate {


     static String generatePrefix(String apiKey,String channel){
        return String.format("apiKey_%s_channel_%s",apiKey,channel);
    }




     static String messageCountKey(String uniqueChannel){
        return String.format("%s_count",uniqueChannel);
    }


    static String messagesIndexKey(String uniqueChannel,int index){
        return String.format("%s_index_%s",uniqueChannel,index);
    }
     static String messagesKey(String uniqueChannel,long index){
        return String.format("%s_index_%s",uniqueChannel,index);
    }

     static String messageClientIndexKey(String uniqueChannel,String clientId){
        return String.format("%s_clientId_%s_index",uniqueChannel,clientId);
    }



}
