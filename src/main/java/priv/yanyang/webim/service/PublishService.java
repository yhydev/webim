package priv.yanyang.webim.service;

import pub.yanyang.common.ResponseBody;

public interface PublishService<T> {

    ResponseBody publish(T t);

}
