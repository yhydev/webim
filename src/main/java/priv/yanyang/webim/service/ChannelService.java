package priv.yanyang.webim.service;

import pub.yanyang.common.ResponseBody;

public interface ChannelService {

    ResponseBody<Long> getChannelClients(String uniqueChannel);


}
