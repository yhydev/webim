package priv.yanyang.webim.service;

import org.springframework.stereotype.Service;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ObservableServiceImpl implements ObservableService {

    private ConcurrentHashMap<String,MessageObservable> obsMap = new ConcurrentHashMap<String,MessageObservable>();

    @Override
    public void removeThenNotifyObservable(String channel, Object o) {
        MessageObservable observable = obsMap.remove(channel);
        if(null != observable){
            synchronized (observable){
                observable.notifyObservers(o);
                observable.deleteObservers();
            }
        }

    }

    @Override
    public void addObserver(String channel, Observer observer) {
        MessageObservable observable = obsMap.get(channel);
        if(null == observable){
            synchronized (obsMap){
                observable = obsMap.get(channel);
                if(null == observable){
                    observable = new MessageObservable();
                    obsMap.put(channel,observable);
                }
            }
        }
        observable.addObserver(observer);
    }

    @Override
    public void deleteObserver(String channel, Observer observer) {
        Observable observable = obsMap.get(channel);
        if(null != observable){
                observable.deleteObserver(observer);
        }
    }
}
