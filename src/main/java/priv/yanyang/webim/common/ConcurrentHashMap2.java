package priv.yanyang.webim.common;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMap2<K,V> extends ConcurrentHashMap<K,V>{

    public V getOrPut(K k,V v){
        V ret = this.get(k);
        if(ret == null){
            ret = v;
            this.put(k,ret);
        }
        return ret;
    }

}
